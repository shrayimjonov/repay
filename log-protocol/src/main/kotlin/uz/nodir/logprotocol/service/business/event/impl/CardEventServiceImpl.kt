package uz.nodir.logprotocol.service.business.event.impl

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import uz.nodir.logprotocol.model.domain.dto.card.request.CardCreditEventRequestDTO
import uz.nodir.logprotocol.service.business.event.CardEventService
import uz.nodir.repayautoconfigure.service.broker.KafkaProducer
import uz.repay.common.config.kafka.Topics
import uz.repay.common.model.dto.request.kafka.CardCreditEventDTO


@Service
class CardEventServiceImpl(
    private val kafkaProducer: KafkaProducer
) : CardEventService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @Async
    override fun saveCardEvent(requestDTO: CardCreditEventRequestDTO) {
        log.info("Received command to handle card event with request dto: {}", requestDTO)

        val eventDTO = CardCreditEventDTO(
            requestDTO.card,
            (requestDTO.amount * 100).toLong(),
            requestDTO.rrn
        )

        /*
                Cause, if card credited, immediately you can't debit.
                If start debit immediately,
                you can take error: Insufficient funds
         */


        Thread.sleep(5_000)


        val producerResult = kafkaProducer.send(Topics.CARD_CREDIT_EVENT_TOPIC, eventDTO.toJson())

        log.info("Finished command to handle card event with result: {}", producerResult)
    }
}