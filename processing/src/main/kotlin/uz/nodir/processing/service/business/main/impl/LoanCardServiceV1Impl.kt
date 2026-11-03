package uz.nodir.processing.service.business.main.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import uz.nodir.processing.model.dto.adapter.request.CardBalanceFetchRequest
import uz.nodir.processing.model.dto.card.response.LoanCardsResult
import uz.nodir.processing.model.entity.Loan
import uz.nodir.processing.model.entity.base.BaseCardEntity
import uz.nodir.processing.service.business.component.BalanceTakerComponent
import uz.nodir.processing.service.business.main.LoanCardService
import uz.nodir.processing.service.dao.LoanDAO
import uz.nodir.processing.service.dao.SwitcherRedisDAO
import uz.nodir.processing.utils.ServiceUtils
import uz.nodir.repayautoconfigure.utils.SecurityUtils
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.Processing
import uz.repay.common.model.enums.ResultState
import java.util.stream.Stream


@Service
class LoanCardServiceV1Impl(
    private val loanDAO: LoanDAO,
    private val switcherRedisDAO: SwitcherRedisDAO,
    private val balanceTakerComponent: BalanceTakerComponent
) : LoanCardService {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun getLoanCards(loanId: Long?): Mono<ResultData<List<LoanCardsResult>>> {
        log.info("Received command to get loan cards by loanId: $loanId")


        return loanIsExist(loanId)
            .flatMap { loanResult ->

                if (!loanResult.status) {
                    log.error("Method finished with error: ${loanResult.error}")
                    return@flatMap Mono.just(ResultData(loanResult.error, false))
                }

                val loan = loanResult.data



                getLoanCardsBalance(loan)
                    .collectList() // Collect Flux<LoanCardsResult> into a Mono<List<LoanCardsResult>>
                    .map { balanceResult -> ResultData.ok(balanceResult) }
            }


    }


    fun loanIsExist(loanId: Long?): Mono<SimpleResponse<Loan>> {
        if (loanId == null)
            return Mono.just(SimpleResponse(ResultState.WrongParams.code, "Loan id is null"))

        val loanResult = loanDAO.findById(loanId)

        return loanResult
            .flatMap { loan ->
                loan?.let {
                    Mono.just(SimpleResponse(it))
                } ?: Mono.just(SimpleResponse(ResultState.DataNotFound.code, "Loan object is null"))
            }
    }

    fun getLoanCardsBalance(loan: Loan): Flux<LoanCardsResult> {
        log.info("Get loan: $loan cards balance")

       // SecurityUtils.checkUserRequestedBranch(loan.extId)

        var stream: Stream<BaseCardEntity> = Stream.empty()
        val cardsMap = HashMap<Processing, MutableList<CardBalanceFetchRequest>>()
        val notHandledCards = mutableListOf<LoanCardsResult>()

        if (loan.loanCards.isNotEmpty()) {
            stream = Stream.concat(stream, loan.loanCards.stream())
        }

        if (loan.guarantors?.isNotEmpty() == true) {
            stream = Stream.concat(stream, loan.guarantors?.flatMap { s -> s.guarantor.guarantorCards }!!.stream())
        }

        stream.forEach { z -> collectCards(z, notHandledCards, cardsMap) }

       return balanceTakerComponent.mergeProcessingResults(cardsMap) // Returns Flux<LoanCardsResult>
            .concatWith(Flux.fromIterable(notHandledCards)) // Combine results from mergeProcessingResults and notHandledCards
            .sort(compareBy(
                { it.state },
                { it.pan },
                { it.holder }
            ))
    }


    private fun collectCards(
        baseCard: BaseCardEntity,
        notHandledCards: MutableList<LoanCardsResult>,
        cardsMap: HashMap<Processing, MutableList<CardBalanceFetchRequest>>
    ) {
        baseCard.card?.let { c ->
            if (c.isActive == true && baseCard.isDeleted == false && baseCard.isConfirmed == true) {

                val processing = c.processing

                processing?.let {
                    switcherRedisDAO.findByName(it.name)
                        .subscribe { s ->
                            s?.let {
                                if (!s.isEnabled) {
                                    log.error("Switcher for processing: $processing is turned off and card: ${c.maskedPan} not added to balance request")
                                    notHandledCards.add(LoanCardsResult(baseCard, false))
                                }

                                cardRequestBuilder(baseCard, processing, cardsMap)
                            }
                        }


                }

            } else {
                log.error("Card: ${c.maskedPan} not active or not confirmed or is deleted")
                notHandledCards.add(LoanCardsResult(baseCard, true))
            }
        }


    }

    private fun cardRequestBuilder(
        baseCard: BaseCardEntity,
        processing: Processing,
        map: HashMap<Processing, MutableList<CardBalanceFetchRequest>>
    ) {
        baseCard.card?.let { card ->

            val cardAccountResult = ServiceUtils.takeDebitAccount(card)

            if (!cardAccountResult.status) {
                log.error("Card not added to balance fetching request, cause: ${cardAccountResult.error}")
                return
            }

            val cardAccount = cardAccountResult.data

            cardAccount?.let {
                val cardTypeResult = ServiceUtils.detectTypeByCard(cardAccount)

                if (!cardTypeResult.status) {
                    log.error("Card not added to balance fetching request, cause: ${cardTypeResult.error}")
                    return
                }

                val cardType = cardTypeResult.data


                val cardRequest = CardBalanceFetchRequest(
                    baseCard,
                    cardType,
                    cardAccount
                )

                map.getOrPut(processing) { mutableListOf() }.add(cardRequest)
            }

        }
    }


}