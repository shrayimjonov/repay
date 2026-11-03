package uz.repay.card.service.core.component.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import uz.repay.card.model.dto.humo.HumoBalanceDetectedEvent;
import uz.repay.card.service.business.component.HumoBalanceScanProcessor;
import uz.repay.card.util.HumoBalanceScanConstants;

@Component
@Slf4j
@RequiredArgsConstructor
public class HumoBalanceScanKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final HumoBalanceScanProcessor scanProcessor;

    @KafkaListener(
            topics = HumoBalanceScanConstants.HUMO_BALANCE_DETECTED_TOPIC,
            groupId = HumoBalanceScanConstants.HUMO_BALANCE_DETECTED_CONSUMER_GROUP_ID,
            concurrency = HumoBalanceScanConstants.HUMO_BALANCE_SCAN_CONSUMER_CONCURRENCY
    )
    public void consume(
            String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {
        var event = parseMessage(message);
        if (event == null) {
            return;
        }

        scanProcessor.process(event, partition);
    }

    private HumoBalanceDetectedEvent parseMessage(String message) {
        if (message == null || message.isBlank()) {
            log.warn("event=humo.balance.detected.consumer.parse_skipped reason=empty_message");
            return null;
        }

        try {
            return objectMapper.readValue(message, HumoBalanceDetectedEvent.class);
        } catch (JsonProcessingException error) {
            log.error("event=humo.balance.detected.consumer.parse_failed messageLength={}",
                    message.length(), error);
            return null;
        } catch (RuntimeException error) {
            log.error("event=humo.balance.detected.consumer.parse_failed reason=unexpected messageLength={}",
                    message.length(), error);
            return null;
        }
    }
}