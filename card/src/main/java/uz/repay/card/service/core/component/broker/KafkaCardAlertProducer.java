package uz.repay.card.service.core.component.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.autopay.request.AutoPayPushRequestDTO;
import uz.repay.card.util.KafkaUtils;

import java.util.UUID;

/**
 * Powered by: Shukhratjon Rayimjonov
 * Date: 22.06.2026
 */

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class KafkaCardAlertProducer {

    KafkaTemplate<String, String> kafkaTemplate;
    ObjectMapper objectMapper;


    @Async
    public void sendToKafka(AutoPayPushRequestDTO requestDTO) {
        try {
            var message = objectMapper.writeValueAsString(requestDTO);
            log.debug("Publishing debtor to Kafka. pan: {}", message);
            kafkaTemplate.send(KafkaUtils.CARD_ALERT_TOPIC, UUID.randomUUID().toString(), message)
                    .thenApply(result -> true)
                    .exceptionally(error -> {
                        log.error("Failed to publish debtor to Kafka. cardPan: {}", requestDTO.getPan(), error);
                        return false;
                    });
        } catch (JsonProcessingException error) {
            log.error("Failed to build Kafka debtor message. cardPan: {}", requestDTO.getPan(), error);
        } catch (Exception error) {
            log.error("Failed to publish debtor to Kafka. cardPan: {}", requestDTO.getPan(), error);
        }
    }
}