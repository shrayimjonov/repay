package uz.repay.card.service.core.component.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.repay.card.model.dto.humo.HumoBalanceScanCard;
import uz.repay.card.service.business.component.HumoBalanceCardRegistry;
import uz.repay.card.service.business.component.HumoBalanceScanPartitionService;
import uz.repay.card.util.HumoBalanceScanConstants;

import java.util.Collection;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class HumoBalanceCardRegistryKafkaConsumer implements ConsumerSeekAware {
    private final ObjectMapper objectMapper;
    private final HumoBalanceCardRegistry cardRegistry;
    private final HumoBalanceScanPartitionService partitionService;

    @KafkaListener(
            topics = HumoBalanceScanConstants.HUMO_BALANCE_SCAN_TOPIC,
            groupId = HumoBalanceScanConstants.HUMO_BALANCE_SCAN_CONSUMER_GROUP_ID,
            concurrency = HumoBalanceScanConstants.HUMO_BALANCE_SCAN_CONSUMER_CONCURRENCY
    )
    public void consume(
            @Payload(required = false) String message,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {
        if (message == null) {
            consumeTombstone(key, partition);
            return;
        }

        var keyCardId = parseCardId(key);
        if (keyCardId == null) {
            log.warn("event=humo.balance.registry.consumer.skipped reason=invalid_key partition={} key={}",
                    partition, key);
            return;
        }

        var card = parseMessage(message);
        if (card == null) {
            return;
        }

        if (!keyCardId.equals(card.getCardId())) {
            log.warn("event=humo.balance.registry.consumer.skipped reason=key_card_mismatch partition={} key={} cardId={}",
                    partition, key, card.getCardId());
            return;
        }

        if (partitionService.partitionFor(card.getCardId()) != partition) {
            log.warn("event=humo.balance.registry.consumer.skipped reason=wrong_partition cardId={} expectedPartition={} actualPartition={}",
                    card.getCardId(), partitionService.partitionFor(card.getCardId()), partition);
            return;
        }

        cardRegistry.upsert(card, partition);
    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        var partitions = assignments.keySet();
        cardRegistry.removePartitions(partitions);
        partitionService.assign(partitions);

        for (var partition : partitions) {
            if (partition != null && HumoBalanceScanConstants.HUMO_BALANCE_SCAN_TOPIC.equals(partition.topic())) {
                callback.seekToBeginning(partition.topic(), partition.partition());
            }
        }
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        cardRegistry.removePartitions(partitions);
        partitionService.revoke(partitions);
    }

    private void consumeTombstone(String key, int partition) {
        var cardId = parseCardId(key);
        if (cardId == null) {
            log.warn("event=humo.balance.registry.consumer.tombstone_skipped reason=invalid_key partition={} key={}",
                    partition, key);
            return;
        }

        if (partitionService.partitionFor(cardId) != partition) {
            log.warn("event=humo.balance.registry.consumer.tombstone_skipped reason=wrong_partition cardId={} expectedPartition={} actualPartition={}",
                    cardId, partitionService.partitionFor(cardId), partition);
            return;
        }

        cardRegistry.remove(cardId, partition);
    }

    private HumoBalanceScanCard parseMessage(String message) {
        if (message.isBlank()) {
            log.warn("event=humo.balance.registry.consumer.parse_skipped reason=empty_message");
            return null;
        }

        try {
            return objectMapper.readValue(message, HumoBalanceScanCard.class);
        } catch (JsonProcessingException error) {
            log.error("event=humo.balance.registry.consumer.parse_failed messageLength={}",
                    message.length(), error);
            return null;
        } catch (RuntimeException error) {
            log.error("event=humo.balance.registry.consumer.parse_failed reason=unexpected messageLength={}",
                    message.length(), error);
            return null;
        }
    }

    private Long parseCardId(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }

        try {
            return Long.valueOf(key);
        } catch (NumberFormatException error) {
            return null;
        }
    }
}
