package uz.nodir.processing.service.adapter.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import uz.nodir.processing.client.VisaMiddlewareWebClient
import uz.nodir.processing.model.dto.adapter.request.CardBalanceFetchRequest
import uz.nodir.processing.model.dto.card.response.LoanCardsResult
import uz.nodir.processing.service.adapter.CardAdapterService
import uz.nodir.processing.service.business.component.DisableCardsComponent
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.CardType
import uz.repay.common.model.enums.Processing
import uz.repay.common.model.enums.ResultState
import uz.repay.common.utils.CardUtils



@Service
class VisaInfoAdapterServiceImpl(
    private val visaMiddlewareWebClient: VisaMiddlewareWebClient,
    private val disableCardsComponent: DisableCardsComponent
) : CardAdapterService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        private const val CARD_NOT_FOUND_MESSAGE = "CARD NOT FOUND"
    }

    override fun getCardsBalance(cardsList: List<CardBalanceFetchRequest>): Mono<SimpleResponse<List<LoanCardsResult>>> {
        log.info("Fetch ${cardsList.size} DAVR_VISA cards balance")

        if (cardsList.isEmpty()) {
            log.info("Request list size empty and will be exited from Cards Balance method!")
            return Mono.just(SimpleResponse(listOf()))
        }


        val panList = cardsList
            .filter { z ->
                if (z.type != CardType.pan) {
                    log.error("DAVR VISA card type must be pan! Now: {}", z.type)
                    return@filter false
                }
                if (z.processing != Processing.DAVR_VISA) {
                    log.error("Processing must be DAVR VISA! Now: {}", z.processing)
                    return@filter false
                }

                z.card?.let { c ->
                    if (!c.matches(CardUtils.regexVisaDAVR().toRegex())) {
                        log.error("DAVR VISA card: {} doesn't match to pan regex!", c)
                        return@filter false
                    }
                }

                return@filter true
            }
            .mapNotNull(CardBalanceFetchRequest::card)
            .toList()

        if (panList.isEmpty()) {
            val message = "After handled, pan list is empty and will be exited from Cards Balance method!"
            log.info(message)
            return Mono.just(SimpleResponse(ResultState.NotProcessed.code, message))
        }


        val responseList = ArrayList<LoanCardsResult>()


        val cardResult = visaMiddlewareWebClient.cardsInfo(panList)

        return cardResult
            .flatMap {
                if (!it.status) {
                    val message =
                        "DAVR VISA fetching cards balance has error: ${it.error.message}"
                    log.error(message)
                    Mono.just(SimpleResponse(ResultState.InternalServerError.code, it.error.message))
                } else {
                    it.data.error?.let { err ->
                        log.error(
                            "DAVR VISA cards balance result has error: ${err.message}",
                        )

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
                            disableCardsComponent.disableVisaNotFoundCards(
                                cardsList,
                                res
                                    .filter { z -> z.pan.equals(CARD_NOT_FOUND_MESSAGE) }
                                    .toList()
                            )


                            cardsList
                                .forEach { z ->
                                    res.stream()
                                        .forEach { u ->
                                            val preparedDTO = LoanCardsResult(z.cardId, u, Processing.DAVR_VISA)
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