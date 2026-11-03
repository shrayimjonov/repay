package uz.nodir.processing.service.business.component

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import uz.nodir.processing.model.dto.adapter.request.CardBalanceFetchRequest
import uz.nodir.processing.model.dto.event.CardDeactivationDTO
import uz.nodir.processing.model.dto.visa.response.param.VisaCardInfoResult
import uz.repay.common.model.dto.response.core.CoreError



@Component
class DisableCardsComponent(
    private val eventPublisher: ApplicationEventPublisher,
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
        private const val NOT_FOUND_CODE = -257
    }


    fun disableUzCardNotFoundCards(cardsList: List<CardBalanceFetchRequest>?, resultTokenList: List<String>?) {
        log.info("Received cards to deactivate! Requested cards size: ${cardsList?.size} and received tokens size: ${resultTokenList?.size}")

        cardsList
            ?.forEach { c ->
                if (resultTokenList
                        ?.stream()
                        ?.noneMatch { r -> c.card.equals(r) }!!
                ) {
                    log.warn("Card not found and will be deactivated: ${c.card}")
                    eventPublisher.publishEvent(CardDeactivationDTO(c.cardId))
                }
            }

        log.info("Exit from disable cards method")
    }


    fun disableUzCardIfTokenNotFound(cardsList: List<CardBalanceFetchRequest>?, error: CoreError?) {
        log.info("Received token to deactivate! With error: $error")

        if (error?.code == NOT_FOUND_CODE) {
            cardsList
                ?.forEach {
                    log.warn("Card not found and will be deactivated: $it")
                    eventPublisher.publishEvent(CardDeactivationDTO(it.cardId))
                }
        }

        log.info("Exit from disable cards method")
    }


    fun setNotActiveCardAndSave(key: Long?) {
        log.info(
            "Card with id: {} returned \"Card not found\" message in response and will be set not active card",
            key
        )
        eventPublisher.publishEvent(CardDeactivationDTO(key))
    }

    fun disableVisaNotFoundCards(
        cardsList: List<CardBalanceFetchRequest>,
        notFoundCardsList: List<VisaCardInfoResult>
    ) {
        log.info("Received cards to deactivate! Requested cards size: ${cardsList.size} and not found pan size: ${notFoundCardsList.size}")

        cardsList
            .forEach { c ->
                if (notFoundCardsList
                        .stream()
                        .anyMatch { r -> r.id.equals(c.card) }
                ) {
                    log.warn("Card not found and will be deactivated: ${c.card}")
                    eventPublisher.publishEvent(CardDeactivationDTO(c.cardId))
                }
            }

        log.info("Exit from disable cards method")
    }

}