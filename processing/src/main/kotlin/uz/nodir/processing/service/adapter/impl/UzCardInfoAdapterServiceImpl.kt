package uz.nodir.processing.service.adapter.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import uz.nodir.processing.client.SVGateWebClient
import uz.nodir.processing.model.dto.adapter.request.CardBalanceFetchRequest
import uz.nodir.processing.model.dto.card.response.LoanCardsResult
import uz.nodir.processing.model.dto.uzCard.response.result.SvGateCardInfoResult
import uz.nodir.processing.service.adapter.CardAdapterService
import uz.nodir.processing.service.business.component.DisableCardsComponent
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.CardType
import uz.repay.common.model.enums.Processing
import uz.repay.common.model.enums.ResultState
import uz.repay.common.utils.CardUtils


@Service
class UzCardInfoAdapterServiceImpl(
    private val svGateWebClient: SVGateWebClient,
    private val disableCardsComponent: DisableCardsComponent
) : CardAdapterService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun getCardsBalance(cardsList: List<CardBalanceFetchRequest>): Mono<SimpleResponse<List<LoanCardsResult>>> {
        log.info("Fetch ${cardsList.size} uzCards balance from SV-GATE")

        if (cardsList.isEmpty()) {
            log.info("Request list size empty and will be exited from Cards Balance method!")
            return Mono.just(SimpleResponse(listOf()))
        }

        val tokens = cardsList
            .filter {

                if (it.type != CardType.token) {
                    log.error("UzCard card type must be token! Now: {}", it.type);
                    return@filter false;
                }

                if (it.processing != Processing.UZCARD) {
                    log.error("Processing must be UzCard! Now: {}", it.processing);
                    return@filter false;
                }

                it.card?.let { c ->
                    if (!c.matches(CardUtils.regexUzCardToken().toRegex())) {
                        log.error("UzCard: {} doesn't match to token regex!", it.card);
                        return@filter false;
                    }
                }

                return@filter true
            }
            .mapNotNull(CardBalanceFetchRequest::card)
            .toList()


        if (tokens.isEmpty()) {
            val message = "After handled, tokens list is empty and will be exited from Cards Balance method!"
            log.info(message)
            return Mono.just(SimpleResponse(ResultState.NotProcessed.code, message))
        }

        val cardBalanceResult = svGateWebClient.cardsGet(tokens)

        val responseList = ArrayList<LoanCardsResult>()

        return cardBalanceResult
            .flatMap {
                if (!it.status) {
                    val message =
                        "SvGate UzCard fetching cards balance has error: ${it.error.message}"
                    log.error(message)
                    Mono.just(SimpleResponse(ResultState.InternalServerError.code, it.error.message))
                } else {
                    it.data.error?.let { err ->
                        log.error(
                            "SvGate UzCard cards balance result has error: ${err.message}",
                        )
                        disableCardsComponent.disableUzCardIfTokenNotFound(cardsList, err)

                        cardsList.forEach { c ->
                            run {
                                c.baseCard?.let { b -> responseList.add(LoanCardsResult(b)) }
                            }
                        }
                        Mono.just(SimpleResponse(ResultState.ThirdPartyError.code, err.message))
                    } ?: Mono.just(it)
                }
            }
            .flatMap {
                it
                    .data
                    .result
                    ?.let { res ->
                        {
                            disableCardsComponent.disableUzCardNotFoundCards(
                                cardsList,
                                res.map(SvGateCardInfoResult::token)
                                    .toList()
                                    .filterNotNull()
                                    .toList()
                            )


                            cardsList
                                .forEach { z ->
                                    res.stream()
                                        .forEach { u ->
                                            val preparedDTO = LoanCardsResult(z.cardId, u, Processing.UZCARD)
                                            responseList.add(preparedDTO)
                                        }
                                }

                        }
                    }




                log.info("UzCards fetch balance from SV-GATE finished")
                Mono.just(SimpleResponse(responseList))
            }
    }
}