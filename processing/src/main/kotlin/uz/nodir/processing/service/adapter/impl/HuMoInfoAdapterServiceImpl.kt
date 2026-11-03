package uz.nodir.processing.service.adapter.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import uz.nodir.processing.client.HuMoMiddlewareWebClient
import uz.nodir.processing.model.dto.adapter.request.CardBalanceFetchRequest
import uz.nodir.processing.model.dto.card.response.LoanCardsResult
import uz.nodir.processing.model.dto.huMo.response.HuMoCardBaseResponse
import uz.nodir.processing.model.dto.huMo.response.result.HuMoCardResult
import uz.nodir.processing.service.adapter.CardAdapterService
import uz.nodir.processing.service.business.component.DisableCardsComponent
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.CardType
import uz.repay.common.model.enums.Processing
import uz.repay.common.utils.CardUtils



@Service
class HuMoInfoAdapterServiceImpl(
    private val huMoMiddlewareWebClient: HuMoMiddlewareWebClient,
    private val disableCardsComponent: DisableCardsComponent
) : CardAdapterService {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun getCardsBalance(cardsList: List<CardBalanceFetchRequest>): Mono<SimpleResponse<List<LoanCardsResult>>> {
        log.info("Fetch ${cardsList.size} HuMo cards balance")

        if (cardsList.isEmpty()) {
            log.info("Request list size empty and will be exited from Cards Balance method!")
            return Mono.just(SimpleResponse(listOf()))
        }

        val responseList = ArrayList<LoanCardsResult>()

        cardsList
            .forEach { z ->
                if (z.type != CardType.pan) {
                    log.error("HuMo card type must be pan! Now: {}", z.type)
                    return@forEach
                }
                if (z.processing != Processing.HUMO) {
                    log.error("Processing must be HuMo! Now: {}", z.processing)
                    return@forEach
                }

                z.card?.let { c ->
                    if (!c.matches(CardUtils.regexHumo().toRegex())) {
                        log.error("HuMo card: {} doesn't match to pan regex!", c)
                        return@forEach
                    }
                }


                val cardResult = huMoMiddlewareWebClient.cardInfo(z.card)

                cardResult
                    .subscribe { f ->
                        if (!f.status) {
                            log.error("HuMo card: ${z.card} fetching error: $f and not added to result list",)
                            return@subscribe

                        }

                        if (f.data.error != null) {
                            log.error("HuMo card: ${z.card} result is FAILED and not added to result list")
                            setNotActiveCardAndSave(z.cardId, f)
                            responseList.add(LoanCardsResult(z.baseCard))
                            return@subscribe
                        }


                        val loanCardsResult = LoanCardsResult(z.cardId, f)
                        log.info("Prepared loan card result: {}", loanCardsResult)
                        responseList.add(loanCardsResult)

                    }

            }



        log.info("HuMo cards fetch balance finished")
        return Mono.just(SimpleResponse(responseList))
    }

    private fun setNotActiveCardAndSave(key: Long?, result: SimpleResponse<HuMoCardBaseResponse<HuMoCardResult>>) {
        if (result.data.error?.message.equals("Card not found")) {
            log.info(
                "Card with id: {} returned \"Card not found\" message in response and will be set not active card",
                key
            )
            disableCardsComponent.setNotActiveCardAndSave(key)
        }
    }
}