package uz.nodir.processing.service.business.bean.impl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import uz.nodir.processing.service.adapter.CardAdapterService
import uz.nodir.processing.service.business.bean.CardBeanRegistrar
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.Processing
import uz.repay.common.model.enums.ResultState



@Service
class CardBeanRegistrarImpl @Autowired constructor(
    cardAdapterServices: List<CardAdapterService>
) : CardBeanRegistrar {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        private val cardInfoAdapterList: MutableMap<Processing, CardAdapterService> = mutableMapOf()
    }

    private fun registerCardInfoAdapterV2Interfaces(cardAdapterV2Services: List<CardAdapterService>) {
        for (service in cardAdapterV2Services) {
            val processingResult = service.getPC()

            processingResult
                .doOnNext {
                    if (!it.status) {
                        log.error("Register bean: {} has error! Cause: {}", service, it.error?.message)
                    }
                }
                .filter { it.status }
                .subscribe {

                    val pc: Processing = it.data
                    log.info("Card adapter v2 bean registered for processing: {}", pc)

                    cardInfoAdapterList[pc] = service
                }

        }
    }

    override fun getCardInfoV2AdapterByPC(pc: Processing): Mono<SimpleResponse<CardAdapterService>> {
        log.info("Get card info adapter v2 for processing: {}", pc)

        val service: CardAdapterService? = cardInfoAdapterList[pc]

        return service?.let {
            Mono.just(SimpleResponse(service))
        } ?: run {
            val message = "Card info adapter not found for processing: $pc"
            log.error(message)
            Mono.just(SimpleResponse(ResultState.DataNotFound.code, message))
        }
    }
}