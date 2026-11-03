package uz.repay.autodebit.service.business.v5;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import uz.repay.autodebit.model.core.response.LoanProcessResult;
import uz.repay.autodebit.model.entity.DebitAttemptStatus;
import uz.repay.autodebit.repository.DebitAttemptRepository;
import uz.repay.autodebit.service.business.AutoPaymentServiceV2;
import uz.repay.autodebit.service.business.implV2.AutodebitConstants;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Component
@Slf4j
public class DebitCycleConsumer {
    private final AutoPaymentServiceV2 autoPaymentService;
    private final ObjectMapper objectMapper;
    private final DebitAttemptRepository attemptRepository;
    private final DebitThroughputLimiter throughputLimiter;

    private final ExecutorService debitExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final Semaphore inFlightLimiter = new Semaphore(AutodebitConstants.MAX_IN_FLIGHT_DEBITS);

    public DebitCycleConsumer(AutoPaymentServiceV2 autoPaymentService,
                              ObjectMapper objectMapper,
                              DebitAttemptRepository attemptRepository,
                              DebitThroughputLimiter throughputLimiter) {
        this.autoPaymentService = autoPaymentService;
        this.objectMapper = objectMapper;
        this.attemptRepository = attemptRepository;
        this.throughputLimiter = throughputLimiter;
    }

    @KafkaListener(
            topics = AutodebitConstants.AUTODEBIT_TOPIC,
            groupId = AutodebitConstants.CYCLE_CONSUMER_GROUP_ID,
            concurrency = AutodebitConstants.CONSUMER_CONCURRENCY,
            batch = "true"
    )
    public void onBatch(List<String> messages) {
        var batchStartNanos = System.nanoTime();
        var futures = new ArrayList<CompletableFuture<Void>>(messages.size());
        var staleCount = 0;
        var invalidCount = 0;

        for (var rawMessage : messages) {
            var message = parse(rawMessage);
            if (message == null || !message.isValid()) {
                invalidCount++;
                continue;
            }

            if (message.isExpired(System.currentTimeMillis())) {
                staleCount++;
                log.debug("event=autodebit.v5.consumer.stale_dropped loanExtId={} cycleId={}",
                        message.loanExtId(), message.cycleId());
                continue;
            }

            try {
                inFlightLimiter.acquire();
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                break;
            }

            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    processSingle(message);
                } finally {
                    inFlightLimiter.release();
                }
            }, debitExecutor));
        }

        awaitBatch(futures);
        log.debug("event=autodebit.v5.consumer.batch_done size={} submitted={} stale={} invalid={} durationMs={}",
                messages.size(), futures.size(), staleCount, invalidCount, elapsedMillis(batchStartNanos));
    }

    private void awaitBatch(List<CompletableFuture<Void>> futures) {
        if (futures.isEmpty()) {
            return;
        }

        try {
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                    .orTimeout(AutodebitConstants.BATCH_JOIN_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .join();
        } catch (Exception error) {
            log.warn("event=autodebit.v5.consumer.batch_timeout message={} - stragglers continue async, offsets commit anyway",
                    error.getMessage());
        }
    }

    private void processSingle(DebitMessage message) {
        try {
            if (attemptRepository.claim(message.loanExtId(), message.cycleId(), message.branchId()) == 0) {
                log.debug("event=autodebit.v5.consumer.claim_lost loanExtId={} cycleId={}",
                        message.loanExtId(), message.cycleId());
                return;
            }

            var cooldownStart = Instant.now().minusMillis(AutodebitConstants.UNKNOWN_OUTCOME_COOLDOWN_MILLIS);
            if (attemptRepository.hasUnknownOutcomeSince(message.loanExtId(), cooldownStart)) {
                markStatus(message, DebitAttemptStatus.SKIPPED_COOLDOWN, "Recent unknown-outcome attempt");
                log.info("event=autodebit.v5.consumer.cooldown_skipped loanExtId={}", message.loanExtId());
                return;
            }

            if (!acquirePermit(message)) {
                return;
            }
            if (message.isExpired(System.currentTimeMillis())) {
                markStatus(message, DebitAttemptStatus.STALE_DROPPED, "Deadline passed while waiting for permit");
                return;
            }
            var result = executeDebit(message);
            recordOutcome(message, result);
        } catch (Exception error) {
            log.error("event=autodebit.v5.consumer.unexpected loanExtId={}", message.loanExtId(), error);
            markStatus(message, DebitAttemptStatus.FAILED_UNKNOWN, "UNEXPECTED: " + error.getMessage());
        }
    }

    private boolean acquirePermit(DebitMessage message) {
        try {
            if (throughputLimiter.acquire(message.deadlineEpochMillis())) {
                return true;
            }
            markStatus(message, DebitAttemptStatus.STALE_DROPPED, "Deadline passed while rate limited");
            return false;
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            markStatus(message, DebitAttemptStatus.FAILED, "Interrupted before debit");
            return false;
        }
    }

    private LoanProcessResult executeDebit(DebitMessage message) {
        try {
            return autoPaymentService.processLoanDirect(message.loanExtId(), message.branchId());
        } catch (Exception error) {
            log.error("event=autodebit.v5.consumer.debit_exception loanExtId={}", message.loanExtId(), error);
            return LoanProcessResult.FAILED_UNKNOWN;
        }
    }

    private void recordOutcome(DebitMessage message, LoanProcessResult result) {
        switch (result != null ? result : LoanProcessResult.FAILED) {
            case SUCCESS -> markStatus(message, DebitAttemptStatus.COMPLETED, null);
            case FAILED_UNKNOWN -> markStatus(message, DebitAttemptStatus.FAILED_UNKNOWN, "Payment outcome unknown");
            case SKIPPED_INACTIVE -> markStatus(message, DebitAttemptStatus.SKIPPED_INACTIVE, "Auto payment inactive");
            default -> markStatus(message, DebitAttemptStatus.FAILED, "Debit failed - see LoanResult log");
        }
    }

    private void markStatus(DebitMessage message, DebitAttemptStatus status, String reason) {
        attemptRepository.markStatus(message.loanExtId(), message.cycleId(), status.name(), reason);
    }

    private DebitMessage parse(String rawMessage) {
        try {
            return objectMapper.readValue(rawMessage, DebitMessage.class);
        } catch (Exception error) {
            log.error("event=autodebit.v5.consumer.parse_failed messageLength={}",
                    rawMessage != null ? rawMessage.length() : 0, error);
            return null;
        }
    }

    @PreDestroy
    public void shutdown() {
        debitExecutor.shutdown();
    }

    private long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}
