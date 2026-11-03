package uz.nodir.processing.service.adapter.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import uz.nodir.processing.client.MasterGateWebClient
import uz.nodir.processing.model.dto.adapter.request.CardBalanceFetchRequest
import uz.nodir.processing.model.dto.card.response.LoanCardsResult
import uz.nodir.processing.service.adapter.CardAdapterService
import uz.nodir.processing.service.business.component.DisableCardsComponent
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.CardType
import uz.repay.common.model.enums.Processing
import uz.repay.common.utils.CardUtils



@Service
class MasterInfoAdapterServiceImpl(
    private val masterGateWebClient: MasterGateWebClient,
    private val disableCardsComponent: DisableCardsComponent
) : CardAdapterService {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun getCardsBalance(cardsList: List<CardBalanceFetchRequest>): Mono<SimpleResponse<List<LoanCardsResult>>> {
        log.info("Fetch ${cardsList.size} Master cards balance")

        if (cardsList.isEmpty()) {
            log.info("Request list size empty and will be exited from Cards Balance method!")
            return Mono.just(SimpleResponse(listOf()))
        }

        val responseList = ArrayList<LoanCardsResult>()


        cardsList
            .forEach { z ->
                if (z.type != CardType.token) {
                    log.error("Master card type must be token! Now: ${z.type}")
                    return@forEach
                }

                if (z.processing != Processing.DAVR_MASTER_CARD) {
                    log.error("Processing must be Master! Now: ${z.processing}")
                    return@forEach
                }

                z.card?.let { c ->
                    if (!c.matches(CardUtils.regexDavrMasterCardToken().toRegex())) {
                        log.error("Master card: $c doesn't match to token regex!")
                        return@forEach
                    }
                }


                val result = masterGateWebClient.balance(z.card)

                result
                    .doOnNext {
                        if (!it.status) {
                            log.error("Master card: ${z.card} fetching error: $result and not added to result list")
                            return@doOnNext
                        }
                    }
                    .filter { it.status }
                    .subscribe {

                        if (it.data.error != null) {
                            log.error("Master card: ${z.card} result is FAILED and not added to result list")
                            disableCardsComponent.setNotActiveCardAndSave(z.cardId)
                            responseList.add(LoanCardsResult(z.baseCard))
                            return@subscribe
                        }


                        val loanCardsResult = LoanCardsResult(z.cardId, it.data.result)
                        log.info("Prepared loan card result: {}", loanCardsResult)
                        responseList.add(loanCardsResult)
                    }


            }

        log.info("Master cards fetch balance finished")
        return Mono.just(SimpleResponse(responseList))

    }
}