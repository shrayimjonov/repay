package uz.repay.card.service.business.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;
import uz.repay.card.model.dto.humo.HumoBalanceScanCard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class HumoBalanceCardRegistry {
    private final ConcurrentMap<Long, HumoBalanceScanCard> cardsById = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Set<Long>> cardIdsByPartition = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Integer> partitionByCardId = new ConcurrentHashMap<>();

    public void upsert(HumoBalanceScanCard card, int partition) {
        if (!isValid(card)) {
            log.warn("event=humo.balance.registry.upsert.skipped reason=invalid_card partition={} cardId={}",
                    partition, card != null ? card.getCardId() : null);
            return;
        }

        var previousPartition = partitionByCardId.put(card.getCardId(), partition);
        if (previousPartition != null && previousPartition != partition) {
            var previousCardIds = cardIdsByPartition.get(previousPartition);
            if (previousCardIds != null) {
                previousCardIds.remove(card.getCardId());
            }
        }

        cardsById.put(card.getCardId(), card);
        cardIdsByPartition.computeIfAbsent(partition, ignored -> ConcurrentHashMap.newKeySet())
                .add(card.getCardId());
    }

    public void remove(Long cardId, int partition) {
        if (cardId == null) {
            return;
        }

        cardsById.remove(cardId);
        var previousPartition = partitionByCardId.remove(cardId);
        var cardIds = cardIdsByPartition.get(partition);
        if (cardIds != null) {
            cardIds.remove(cardId);
        }
        if (previousPartition != null && previousPartition != partition) {
            var previousCardIds = cardIdsByPartition.get(previousPartition);
            if (previousCardIds != null) {
                previousCardIds.remove(cardId);
            }
        }
    }

    public void removePartitions(Collection<TopicPartition> partitions) {
        if (partitions == null || partitions.isEmpty()) {
            return;
        }

        for (var partition : partitions) {
            if (partition == null) {
                continue;
            }

            var removedCardIds = cardIdsByPartition.remove(partition.partition());
            if (removedCardIds == null || removedCardIds.isEmpty()) {
                continue;
            }

            for (var cardId : removedCardIds) {
                cardsById.remove(cardId);
                partitionByCardId.remove(cardId);
            }
            log.info("event=humo.balance.registry.partition_cleared partition={} cards={}",
                    partition.partition(), removedCardIds.size());
        }
    }

    public List<HumoBalanceScanCard> cardsSnapshot(Collection<Integer> partitions) {
        if (partitions == null || partitions.isEmpty()) {
            return List.of();
        }

        var result = new ArrayList<HumoBalanceScanCard>();
        for (var partition : partitions) {
            var cardIds = cardIdsByPartition.get(partition);
            if (cardIds == null || cardIds.isEmpty()) {
                continue;
            }

            for (var cardId : cardIds) {
                var card = cardsById.get(cardId);
                if (card != null) {
                    result.add(card);
                }
            }
        }
        return result;
    }

    public Set<Long> cardIdsSnapshot(Collection<Integer> partitions) {
        if (partitions == null || partitions.isEmpty()) {
            return Set.of();
        }

        var result = new HashSet<Long>();
        for (var partition : partitions) {
            var cardIds = cardIdsByPartition.get(partition);
            if (cardIds != null) {
                result.addAll(cardIds);
            }
        }
        return result;
    }

    public int size() {
        return cardsById.size();
    }

    private boolean isValid(HumoBalanceScanCard card) {
        return card != null
                && card.getCardId() != null
                && card.getEncryptedPan() != null
                && !card.getEncryptedPan().isBlank();
    }
}
