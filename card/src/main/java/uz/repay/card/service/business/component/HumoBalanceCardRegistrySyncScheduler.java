package uz.repay.card.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.repay.card.model.dto.humo.HumoBalanceScanCard;
import uz.repay.card.repository.projection.HumoAutoDebitCardProjection;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.core.component.broker.HumoBalanceScanKafkaProducer;
import uz.repay.card.util.HumoBalanceScanConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class HumoBalanceCardRegistrySyncScheduler {
    private final CardDAO cardDAO;
    private final HumoBalanceScanKafkaProducer kafkaProducer;
    private final HumoBalanceScanPartitionService partitionService;
    private final HumoBalanceCardRegistry cardRegistry;

/*    @Scheduled(
            fixedRate = HumoBalanceScanConstants.HUMO_BALANCE_REGISTRY_SYNC_FIXED_RATE_MILLIS,
            initialDelay = HumoBalanceScanConstants.HUMO_BALANCE_REGISTRY_SYNC_INITIAL_DELAY_MILLIS
    )*/
    public void syncActiveHumoCardsToRegistry() {
        var startNanos = System.nanoTime();
        var partitions = partitionService.assignedScanPartitions();
        if (partitions.isEmpty()) {
            log.warn("event=humo.balance.registry.sync.skipped reason=no_assigned_partitions");
            return;
        }

        try {
            var activeCards = cardDAO.findActiveAutoDebitHumoCardsByKafkaPartitions(
                    partitions,
                    HumoBalanceScanConstants.HUMO_BALANCE_SCAN_TOPIC_PARTITIONS
            );

            var activeCardIds = new HashSet<Long>();
            var registryCards = new ArrayList<HumoBalanceScanCard>();
            var now = System.currentTimeMillis();
            var skipped = 0;

            if (activeCards != null) {
                for (var card : activeCards) {
                    if (!isValid(card) || !partitionService.ownsCard(card.getCardId())) {
                        skipped++;
                        continue;
                    }

                    activeCardIds.add(card.getCardId());
                    registryCards.add(new HumoBalanceScanCard(
                            card.getCardId(),
                            card.getPan(),
                            card.getMaskedPan(),
                            now
                    ));
                }
            }

            var submittedUpserts = kafkaProducer.publishRegistryCards(registryCards);
            var submittedDeletes = publishStaleCardTombstones(partitions, activeCardIds);

            log.info("event=humo.balance.registry.sync.completed partitions={} activeCards={} upserts={} tombstones={} skipped={} registrySize={} durationMs={}",
                    partitions, activeCardIds.size(), submittedUpserts, submittedDeletes, skipped,
                    cardRegistry.size(), elapsedMillis(startNanos));
        } catch (RuntimeException error) {
            log.error("event=humo.balance.registry.sync.exception partitions={} durationMs={} message={}",
                    partitions, elapsedMillis(startNanos), error.getMessage(), error);
        }
    }

    private int publishStaleCardTombstones(List<Integer> partitions, Set<Long> activeCardIds) {
        var localCardIds = cardRegistry.cardIdsSnapshot(partitions);
        var submittedDeletes = 0;

        for (var cardId : localCardIds) {
            if (!activeCardIds.contains(cardId) && kafkaProducer.publishRegistryTombstone(cardId)) {
                submittedDeletes++;
            }
        }

        return submittedDeletes;
    }

    private boolean isValid(HumoAutoDebitCardProjection card) {
        return card != null
                && card.getCardId() != null
                && card.getPan() != null
                && !card.getPan().isBlank();
    }

    private long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}
