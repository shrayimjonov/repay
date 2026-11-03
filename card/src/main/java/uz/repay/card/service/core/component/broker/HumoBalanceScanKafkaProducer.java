package uz.repay.card.service.core.component.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.humo.HumoBalanceDetectedEvent;
import uz.repay.card.model.dto.humo.HumoBalanceScanCard;
import uz.repay.card.service.business.component.HumoBalanceScanPartitionService;
import uz.repay.card.util.HumoBalanceScanConstants;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@RequiredArgsConstructor
public class HumoBalanceScanKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final HumoBalanceScanPartitionService partitionService;

    public int publishRegistryCards(Collection<HumoBalanceScanCard> cards) {
        if (cards == null || cards.isEmpty()) {
            return 0;
        }

        var submitted = 0;
        for (var card : cards) {
            if (publishRegistryCard(card)) {
                submitted++;
            }
        }
        return submitted;
    }

    public boolean publishRegistryCard(HumoBalanceScanCard card) {
        if (card == null || card.getCardId() == null) {
            return false;
        }

        return publishAsync(
                card.getCardId(),
                card
        );
    }

    public boolean publishRegistryTombstone(Long cardId) {
        if (cardId == null) {
            return false;
        }

        try {
            var partition = partitionService.partitionFor(cardId);
            var key = String.valueOf(cardId);
            kafkaTemplate.send(HumoBalanceScanConstants.HUMO_BALANCE_SCAN_TOPIC, partition, key, (String) null)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("event=humo.balance.registry.kafka.delete.failed cardId={} topic={} partition={} reason=kafka_send",
                                    cardId, HumoBalanceScanConstants.HUMO_BALANCE_SCAN_TOPIC, partition, error);
                        }
                    });
            return true;
        } catch (RuntimeException error) {
            log.error("event=humo.balance.registry.kafka.delete.failed cardId={} topic={} reason=unexpected",
                    cardId, HumoBalanceScanConstants.HUMO_BALANCE_SCAN_TOPIC, error);
            return false;
        }
    }

    public boolean publishDetectedEvent(HumoBalanceDetectedEvent event) {
        if (event == null || event.getCardId() == null) {
            return false;
        }

        return publishDetected(event);
    }

    private boolean publishAsync(Long cardId, Object payload) {
        try {
            var message = objectMapper.writeValueAsString(payload);
            var partition = partitionService.partitionFor(cardId);
            var key = String.valueOf(cardId);
            kafkaTemplate.send(HumoBalanceScanConstants.HUMO_BALANCE_SCAN_TOPIC, partition, key, message)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("event={}.failed cardId={} topic={} partition={} reason=kafka_send",
                                    "humo.balance.registry.kafka.upsert", cardId, HumoBalanceScanConstants.HUMO_BALANCE_SCAN_TOPIC, partition, error);
                        }
                    });
            return true;
        } catch (JsonProcessingException error) {
            log.error("event={}.failed cardId={} topic={} reason=serialization", "humo.balance.registry.kafka.upsert", cardId, HumoBalanceScanConstants.HUMO_BALANCE_SCAN_TOPIC, error);
        } catch (RuntimeException error) {
            log.error("event={}.failed cardId={} topic={} reason=unexpected", "humo.balance.registry.kafka.upsert", cardId, HumoBalanceScanConstants.HUMO_BALANCE_SCAN_TOPIC, error);
        }

        return false;
    }

    private boolean publishDetected(HumoBalanceDetectedEvent event) {
        try {
            var message = objectMapper.writeValueAsString(event);
            var partition = partitionService.partitionFor(event.getCardId());
            var key = String.valueOf(event.getCardId());
            kafkaTemplate.send(HumoBalanceScanConstants.HUMO_BALANCE_DETECTED_TOPIC, partition, key, message)
                    .get(HumoBalanceScanConstants.HUMO_BALANCE_DETECTED_KAFKA_SEND_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            return true;
        } catch (JsonProcessingException error) {
            log.error("event=humo.balance.detected.kafka.publish.failed cardId={} topic={} reason=serialization",
                    event.getCardId(), HumoBalanceScanConstants.HUMO_BALANCE_DETECTED_TOPIC, error);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            log.error("event=humo.balance.detected.kafka.publish.failed cardId={} topic={} reason=interrupted",
                    event.getCardId(), HumoBalanceScanConstants.HUMO_BALANCE_DETECTED_TOPIC, error);
        } catch (ExecutionException error) {
            log.error("event=humo.balance.detected.kafka.publish.failed cardId={} topic={} reason=kafka_send_error: {}",
                    event.getCardId(), HumoBalanceScanConstants.HUMO_BALANCE_DETECTED_TOPIC,
                    error.getCause() != null ? error.getCause() : error);
        } catch (TimeoutException error) {
            log.error("event=humo.balance.detected.kafka.publish.failed cardId={} topic={} reason=timeout timeoutMs={}",
                    event.getCardId(), HumoBalanceScanConstants.HUMO_BALANCE_DETECTED_TOPIC,
                    HumoBalanceScanConstants.HUMO_BALANCE_DETECTED_KAFKA_SEND_TIMEOUT_MILLIS, error);
        } catch (RuntimeException error) {
            log.error("event=humo.balance.detected.kafka.publish.failed cardId={} topic={} reason=unexpected",
                    event.getCardId(), HumoBalanceScanConstants.HUMO_BALANCE_DETECTED_TOPIC, error);
        }

        return false;
    }
}
