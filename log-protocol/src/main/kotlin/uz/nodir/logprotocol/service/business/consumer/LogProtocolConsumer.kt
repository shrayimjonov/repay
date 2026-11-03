package uz.nodir.logprotocol.service.business.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uz.nodir.logprotocol.model.domain.LogProtocol
import uz.nodir.logprotocol.service.core.dao.LogProtocolDAO
import uz.nodir.repayautoconfigure.service.RecordToClassConverter
import uz.repay.common.config.kafka.Topics
import uz.repay.common.model.dto.request.kafka.LogProtocolEventDTO
import uz.repay.common.model.dto.response.core.SimpleResponse


@KafkaListener(
    groupId = "log-protocol-save",
    topics = [Topics.LOG_PROTOCOL],
    // containerFactory = "fooContainerFactory" see https://sylhare.github.io/2023/09/15/Kafka-with-springboot-in-kotlin.html
)
@Component
class LogProtocolConsumer(
    private val logProtocolDAO: LogProtocolDAO,
    private val recordToClassConverter: RecordToClassConverter
) {

    @Autowired
    private lateinit var mapper: ObjectMapper

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @KafkaHandler
    fun saveLog(@Payload value: String) {
        log.info("Received log protocol event dto: {}", value)

        val convertResult = recordToClassConverter.convertMessageV2(value, LogProtocolEventDTO::class.java)

        if (!convertResult.status) {
            log.error("Convert result is unsuccessful and will be returned")
            return
        }

        val logEventDTO = convertResult.data

        val protocol = LogProtocol(
            loanExtId = logEventDTO.loanExtId,
            type = logEventDTO.type,
            request = logEventDTO.request,
            ipAddress = logEventDTO.ipAddress,
            createdBy = logEventDTO.username
        )

        logProtocolDAO.save(protocol)
    }

    @Deprecated(message = "Deprecated cause is using RecordToClassConverter")
    private fun <T> convertMessage(message: String, clazz: Class<T>): SimpleResponse<T> {
        try {
            return SimpleResponse<T>(mapper.readValue(message, clazz))
        } catch (e: Exception) {
            log.error("mapper converting error: {}", e.message)
            return SimpleResponse(500, e.message)
        }
    }
}