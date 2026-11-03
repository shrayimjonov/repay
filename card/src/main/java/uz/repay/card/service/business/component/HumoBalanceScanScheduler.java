package uz.repay.card.service.business.component;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.repay.card.model.dto.humo.HumoBalanceScanCard;
import uz.repay.card.service.core.SwitcherRedisService;
import uz.repay.card.util.HumoBalanceScanConstants;
import uz.repay.common.model.enums.Processing;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
@RequiredArgsConstructor
public class HumoBalanceScanScheduler {
    private final SwitcherRedisService switcherRedisService;
    private final HumoBalanceScanPartitionService partitionService;
    private final HumoBalanceCardRegistry cardRegistry;
    private final HumoBalanceCardScanner cardScanner;
    private final ExecutorService scanExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final Semaphore scanPermits = new Semaphore(HumoBalanceScanConstants.HUMO_BALANCE_SCAN_MAX_IN_FLIGHT_PER_INSTANCE);
    private final Set<Long> inProgressCards = ConcurrentHashMap.newKeySet();
    private final AtomicLong scanCursor = new AtomicLong();

    @PreDestroy
    public void shutdownScanExecutor() {
        scanExecutor.shutdown();
    }

//    @Scheduled(fixedRate = HumoBalanceScanConstants.HUMO_BALANCE_SCAN_FIXED_RATE_MILLIS)
    public void scanRegisteredHumoCards() {
        var startNanos = System.nanoTime();
        if (isHumoDisabled()) {
            return;
        }

        var partitions = partitionService.assignedScanPartitions();
        if (partitions.isEmpty()) {
            log.warn("event=humo.balance.scan.skipped reason=no_assigned_partitions");
            return;
        }

        var cards = selectCardsForThisRun(cardRegistry.cardsSnapshot(partitions));
        if (cards.isEmpty()) {
            log.info("event=humo.balance.scan.completed partitions={} cards=0 submitted=0 inFlight={} registrySize={} durationMs={}",
                    partitions, inProgressCards.size(), cardRegistry.size(), elapsedMillis(startNanos));
            return;
        }

        var submitted = 0;
        var skippedInProgress = 0;
        var skippedInvalid = 0;

        for (var index = 0; index < cards.size(); index++) {
            var card = cards.get(index);
            var assignmentVersion = partitionService.assignmentVersion();
            if (!isValid(card) || !partitionService.ownsCard(card.getCardId(), assignmentVersion)) {
                skippedInvalid++;
                continue;
            }

            if (!inProgressCards.add(card.getCardId())) {
                skippedInProgress++;
                continue;
            }

            if (submitScan(card, assignmentVersion, scanDelayMillis(index, cards.size()))) {
                submitted++;
            }
        }

        log.info("event=humo.balance.scan.submitted partitions={} selectedCards={} submitted={} skippedInProgress={} skippedInvalid={} inFlight={} registrySize={} maxCardsPerRun={} maxInFlight={} durationMs={}",
                partitions, cards.size(), submitted, skippedInProgress, skippedInvalid,
                inProgressCards.size(), cardRegistry.size(),
                HumoBalanceScanConstants.HUMO_BALANCE_SCAN_MAX_CARDS_PER_INSTANCE_PER_RUN,
                HumoBalanceScanConstants.HUMO_BALANCE_SCAN_MAX_IN_FLIGHT_PER_INSTANCE,
                elapsedMillis(startNanos));
    }

    private boolean submitScan(HumoBalanceScanCard card, long assignmentVersion, long delayMillis) {
        try {
            scanExecutor.execute(() -> {
                var permitAcquired = false;
                try {
                    sleepBeforeScan(delayMillis);
                    scanPermits.acquire();
                    permitAcquired = true;
                    cardScanner.scan(card, partitionService.partitionFor(card.getCardId()), assignmentVersion);
                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                    log.error("event=humo.balance.scan.worker.interrupted cardId={} maskedPan={}",
                            card.getCardId(), card.getMaskedPan(), error);
                } finally {
                    inProgressCards.remove(card.getCardId());
                    if (permitAcquired) {
                        scanPermits.release();
                    }
                }
            });
            return true;
        } catch (RejectedExecutionException error) {
            inProgressCards.remove(card.getCardId());
            log.error("event=humo.balance.scan.submit.rejected cardId={} maskedPan={}",
                    card.getCardId(), card.getMaskedPan(), error);
            return false;
        }
    }

    private List<HumoBalanceScanCard> selectCardsForThisRun(List<HumoBalanceScanCard> cards) {
        if (cards == null || cards.isEmpty()) {
            return List.of();
        }

        cards.sort(Comparator.comparing(HumoBalanceScanCard::getCardId));
        var limit = Math.min(cards.size(), HumoBalanceScanConstants.HUMO_BALANCE_SCAN_MAX_CARDS_PER_INSTANCE_PER_RUN);
        if (limit == cards.size()) {
            return cards;
        }

        var start = Math.floorMod(scanCursor.getAndAdd(limit), cards.size());
        var selectedCards = new java.util.ArrayList<HumoBalanceScanCard>(limit);
        for (var index = 0; index < limit; index++) {
            selectedCards.add(cards.get((start + index) % cards.size()));
        }
        return selectedCards;
    }

    private long scanDelayMillis(int index, int selectedCards) {
        if (index <= 0 || selectedCards <= 1) {
            return 0L;
        }

        return Math.max(0L, HumoBalanceScanConstants.HUMO_BALANCE_SCAN_FIXED_RATE_MILLIS * index / selectedCards);
    }

    private void sleepBeforeScan(long delayMillis) throws InterruptedException {
        if (delayMillis > 0) {
            Thread.sleep(delayMillis);
        }
    }

    private boolean isValid(HumoBalanceScanCard card) {
        return card != null
                && card.getCardId() != null
                && card.getEncryptedPan() != null
                && !card.getEncryptedPan().isBlank();
    }

    private boolean isHumoDisabled() {
        try {
            var switcher = switcherRedisService.findByName(Processing.HUMO.name());
            return switcher == null || !switcher.getIsEnabled();
        } catch (RuntimeException error) {
            log.error("event=humo.balance.scan.skipped reason=switcher_unavailable processing={}",
                    Processing.HUMO, error);
            return true;
        }
    }

    private long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}
