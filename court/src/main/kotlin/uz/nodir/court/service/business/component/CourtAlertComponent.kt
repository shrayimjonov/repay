package uz.nodir.court.service.business.component

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uz.nodir.repayautoconfigure.service.broker.KafkaProducer
import uz.repay.common.config.kafka.Topics
import uz.repay.common.model.dto.request.kafka.CourtEventDTO


@Component
class CourtAlertComponent(
    private val kafkaProducer: KafkaProducer
) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java);
    }

    fun sendNotification(loanExtId: String, phone: String, message: String) {
        log.info("Start sending alert notification to broker: {} {} {}", loanExtId, phone, message)


        val result = kafkaProducer.send(
            Topics.NOTIFICATION_COURT_ALERT,
            CourtEventDTO(
                loanExtId, phone, message
            )
                .toJson()
        )

        log.info("End broker result with status: {}", result.status)
    }
}