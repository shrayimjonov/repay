package uz.nodir.processing.service.business.component

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import uz.nodir.processing.model.dto.adapter.request.CardBalanceFetchRequest
import uz.nodir.processing.model.dto.card.response.LoanCardsResult
import uz.nodir.processing.service.adapter.CardAdapterService
import uz.nodir.processing.service.business.bean.CardBeanRegistrar
import uz.repay.common.model.enums.Processing
import java.util.concurrent.CompletableFuture


@Service
class BalanceTakerComponent(
    private val cardBeanRegistrar: CardBeanRegistrar
) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @Transactional
    fun mergeProcessingResultsV2(cardsBalanceRequest: HashMap<Processing, MutableList<CardBalanceFetchRequest>>): MutableList<LoanCardsResult> {
        val cards = mutableListOf<LoanCardsResult>()
        val tasks: MutableList<CompletableFuture<Void>> = ArrayList()

        cardsBalanceRequest
            .forEach { r ->
                tasks.add(CompletableFuture.runAsync {
                    takeBalance(
                        r.key,
                        r.value,
                        cards
                    )
                })
            }


        tasks.forEach { z -> z.join() }
        return cards
    }

    @Transactional
    fun mergeProcessingResults(
        cardsBalanceRequest: HashMap<Processing, MutableList<CardBalanceFetchRequest>>
    ): Flux<LoanCardsResult> {
        return Mono.fromCallable {
            // Выполните обработку и верните результат
            val results = mutableListOf<LoanCardsResult>()
            val cards = mutableListOf<LoanCardsResult>()
            cardsBalanceRequest
                .forEach { r ->
                        takeBalance(
                            r.key,
                            r.value,
                            cards
                        )

                }

            results
        }
            .flatMapMany { Flux.fromIterable(it) } // Преобразуем результаты в Flux
            .subscribeOn(Schedulers.boundedElastic())
    }

//    private fun takeBalance(
//        processing: Processing,
//        requests: MutableList<CardBalanceFetchRequest>,
//        cards: MutableList<LoanCardsResult>
//    ) {
//        cardBeanRegistrar.getCardInfoV2AdapterByPC(processing)
//            .flatMap {
//                if (it.status) {
//                    log.error("Card adapter result has error: ${it.error}")
//                    return@flatMap Mono.empty<Void>()
//                }
//
//                val service = it.data
//
//
//                service.getCardsBalance(requests)
//            }
//            .flatMap { s->
//                if (!s.status) {
//                    log.error("Get Balance result has error: ${s.error}")
//                    return@flatMap
//                }
//
//                val balanceResult = s.data
//
//                if (balanceResult.isEmpty()) {
//                    log.warn("Get Balance result is empty")
//                    return@flatMap
//                }
//
//                cards.addAll(balanceResult)
//            }
//    }

    private fun takeBalance(
        processing: Processing,
        requests: MutableList<CardBalanceFetchRequest>,
        cards: MutableList<LoanCardsResult>
    ) {
        cardBeanRegistrar.getCardInfoV2AdapterByPC(processing)
            .flatMap { result ->
                if (!result.status) {
                    log.error("Card adapter result has error: ${result.error}")
                    return@flatMap Mono.empty<CardAdapterService>() // Exit the reactive chain early
                }
                Mono.just(result.data) // Replace `YourServiceType` with the actual type of `result.data`
            }
            .flatMap { service ->
                service.getCardsBalance(requests) // Ensure this method exists and returns a reactive type
            }
            .flatMap { balanceResult ->
                if (!balanceResult.status) {
                    log.error("Get Balance result has error: ${balanceResult.error}")
                    return@flatMap Mono.empty<Void>() // Exit the chain if an error is encountered
                }
                if (balanceResult.data.isEmpty()) {
                    log.warn("Get Balance result is empty")
                    return@flatMap Mono.empty<Void>() // Exit the chain if no data is returned
                }
                cards.addAll(balanceResult.data)
                Mono.empty() // Continue the chain
            }
            .subscribe() // Trigger the reactive flow
    }
}