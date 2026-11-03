package uz.repay.autodebit.service.business.v5;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.repay.autodebit.model.entity.DebitCycle;
import uz.repay.autodebit.model.entity.DebitCycleStatus;
import uz.repay.autodebit.model.loan.response.DebtLoansResult;
import uz.repay.autodebit.repository.DebitAttemptRepository;
import uz.repay.autodebit.repository.DebitCycleRepository;
import uz.repay.autodebit.service.business.AutoPaymentStateControlService;
import uz.repay.autodebit.service.business.implV2.AutodebitConstants;
import uz.repay.autodebit.service.web.LoanFeignService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebitCyclePublisher {
    private static final int PUBLISH_CHUNK_SIZE = 5_000;
    private static final int PURGE_BATCH_SIZE = 10_000;

    private final LoanFeignService loanFeignService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final DebitCycleRepository cycleRepository;
    private final DebitAttemptRepository attemptRepository;
    private final AutoPaymentStateControlService stateControlService;

    private final AtomicLong roundRobinCounter = new AtomicLong();
    private volatile int cachedPartitionCount = -1;

    @Scheduled(fixedRate = AutodebitConstants.CYCLE_FIXED_RATE_MILLIS)
    @SchedulerLock(name = "autodebit-cycle-publisher", lockAtMostFor = "PT9M", lockAtLeastFor = "PT1M")
    public void publishCycle() {
        if (!stateControlService.isActive()) {
            log.info("event=autodebit.v5.cycle.skipped reason=inactive");
            return;
        }

        var startNanos = System.nanoTime();
        try {
            runCycle(startNanos);
        } catch (RuntimeException error) {
            log.error("event=autodebit.v5.cycle.failed durationMs={}", elapsedMillis(startNanos), error);
        }
    }

    private void runCycle(long startNanos) {
        runJanitor();

        if (cycleRepository.hasActiveCycle()) {
            log.warn("event=autodebit.v5.cycle.skipped reason=previous_cycle_running");
            return;
        }

        var cycleId = UUID.randomUUID().toString();
        var deadline = Instant.now().plusMillis(AutodebitConstants.PROCESSING_INTERVAL_MILLIS);
        cycleRepository.save(DebitCycle.builder()
                .cycleId(cycleId)
                .startedAt(Instant.now())
                .deadline(deadline)
                .expectedCount(0)
                .status(DebitCycleStatus.RUNNING)
                .build());

        var debtors = loadDebtorSnapshot();
        if (debtors.isEmpty()) {
            cycleRepository.deleteById(cycleId);
            log.warn("event=autodebit.v5.cycle.empty cycleId={} durationMs={}", cycleId, elapsedMillis(startNanos));
            return;
        }

        var publishedCount = publishDebtors(cycleId, deadline, debtors);
        cycleRepository.updateExpectedCount(cycleId, publishedCount);

        log.info("event=autodebit.v5.cycle.published cycleId={} debtors={} published={} failed={} deadline={} durationMs={}",
                cycleId, debtors.size(), publishedCount, debtors.size() - publishedCount, deadline, elapsedMillis(startNanos));
    }

    @Scheduled(fixedRate = AutodebitConstants.MAINTENANCE_FIXED_RATE_MILLIS)
    @SchedulerLock(name = "autodebit-attempt-purge", lockAtMostFor = "PT50M", lockAtLeastFor = "PT1M")
    public void purgeExpiredData() {
        var cutoff = Instant.now().minus(AutodebitConstants.ATTEMPT_RETENTION_DAYS, ChronoUnit.DAYS);
        var totalDeleted = 0;
        int deleted;
        do {
            deleted = attemptRepository.purgeBatchOlderThan(cutoff, PURGE_BATCH_SIZE);
            totalDeleted += deleted;
        } while (deleted == PURGE_BATCH_SIZE);

        var deletedCycles = cycleRepository.deleteOlderThan(cutoff);
        if (totalDeleted > 0 || deletedCycles > 0) {
            log.info("event=autodebit.v5.purge.completed attempts={} cycles={} cutoff={}", totalDeleted, deletedCycles, cutoff);
        }
    }

    private void runJanitor() {
        var expiredCycles = cycleRepository.expireOverdueCycles();
        var expiredClaims = attemptRepository.expireStaleProcessing(
                Instant.now().minusMillis(AutodebitConstants.STALE_PROCESSING_AFTER_MILLIS));
        if (expiredCycles > 0 || expiredClaims > 0) {
            log.warn("event=autodebit.v5.janitor.completed expiredCycles={} expiredStaleClaims={}", expiredCycles, expiredClaims);
        }
    }

    private List<DebtLoansResult> loadDebtorSnapshot() {
        var pageSize = AutodebitConstants.DEBTOR_LOAD_PAGE_SIZE;
        var debtorsByLoanId = new LinkedHashMap<String, DebtLoansResult>();
        long pageNumber = 0;

        while (true) {
            var response = loanFeignService.getAllDebtLoansPageV2(pageSize, pageNumber);
            if (response == null || !response.hasBody() || response.getBody() == null) {
                log.error("event=autodebit.v5.debtors.load_failed reason=no_body pageNumber={}", pageNumber);
                break;
            }

            var body = response.getBody();
            if (body.getError() != null) {
                log.error("event=autodebit.v5.debtors.load_failed reason=loan_service_error pageNumber={} message={}",
                        pageNumber, body.getError().getMessage());
                break;
            }

            var page = body.getResult();
            if (page == null) {
                log.error("event=autodebit.v5.debtors.load_failed reason=null_page pageNumber={}", pageNumber);
                break;
            }

            var pageData = page.getDebtLoans() != null ? page.getDebtLoans() : List.<DebtLoansResult>of();
            for (var debtor : pageData) {
                if (isExecutable(debtor)) {
                    debtorsByLoanId.putIfAbsent(debtor.getLoanExtId(), debtor);
                }
            }

            if (page.isLast() || pageData.size() < pageSize) {
                break;
            }
            pageNumber++;
        }

        return new ArrayList<>(debtorsByLoanId.values());
    }

    private int publishDebtors(String cycleId, Instant deadline, List<DebtLoansResult> debtors) {
        var deadlineEpochMillis = deadline.toEpochMilli();
        var publishedCount = 0;

        for (var chunkStart = 0; chunkStart < debtors.size(); chunkStart += PUBLISH_CHUNK_SIZE) {
            var chunk = debtors.subList(chunkStart, Math.min(debtors.size(), chunkStart + PUBLISH_CHUNK_SIZE));
            publishedCount += publishChunk(cycleId, deadlineEpochMillis, chunk);
        }
        return publishedCount;
    }

    private int publishChunk(String cycleId, long deadlineEpochMillis, List<DebtLoansResult> chunk) {
        var futures = new ArrayList<CompletableFuture<?>>(chunk.size());
        var serializationFailed = 0;

        for (var debtor : chunk) {
            var future = sendDebtor(cycleId, deadlineEpochMillis, debtor);
            if (future != null) {
                futures.add(future);
            } else {
                serializationFailed++;
            }
        }

        var brokerFailed = 0;
        for (var future : futures) {
            try {
                future.orTimeout(AutodebitConstants.PUBLISH_CHUNK_JOIN_TIMEOUT_SECONDS, TimeUnit.SECONDS).join();
            } catch (Exception error) {
                brokerFailed++;
            }
        }

        if (serializationFailed > 0 || brokerFailed > 0) {
            log.warn("event=autodebit.v5.publish.partial chunkSize={} serializationFailed={} brokerFailed={}",
                    chunk.size(), serializationFailed, brokerFailed);
        }
        return chunk.size() - serializationFailed - brokerFailed;
    }

    private CompletableFuture<?> sendDebtor(String cycleId, long deadlineEpochMillis, DebtLoansResult debtor) {
        try {
            var message = objectMapper.writeValueAsString(
                    new DebitMessage(debtor.getLoanExtId(), debtor.getBranch(), cycleId, deadlineEpochMillis));
            var partition = nextPartition();
            return partition >= 0
                    ? kafkaTemplate.send(AutodebitConstants.AUTODEBIT_TOPIC, partition, debtor.getLoanExtId(), message)
                    : kafkaTemplate.send(AutodebitConstants.AUTODEBIT_TOPIC, debtor.getLoanExtId(), message);
        } catch (Exception error) {
            log.error("event=autodebit.v5.publish.failed loanExtId={}", debtor.getLoanExtId(), error);
            return null;
        }
    }

    private int nextPartition() {
        var partitionCount = cachedPartitionCount;
        if (partitionCount <= 0) {
            try {
                var partitions = kafkaTemplate.partitionsFor(AutodebitConstants.AUTODEBIT_TOPIC);
                partitionCount = partitions.size();
                cachedPartitionCount = partitionCount;
            } catch (Exception error) {
                log.warn("event=autodebit.v5.publish.partition_lookup_failed message={}", error.getMessage());
                return -1;
            }
        }

        return partitionCount > 0
                ? (int) (roundRobinCounter.getAndIncrement() % partitionCount)
                : -1;
    }

    private boolean isExecutable(DebtLoansResult debtor) {
        return debtor != null
                && debtor.getLoanExtId() != null && !debtor.getLoanExtId().isBlank()
                && debtor.getBranch() != null && !debtor.getBranch().isBlank();
    }

    private long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}