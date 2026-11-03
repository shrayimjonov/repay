package uz.repay.autodebit.service.business.implV2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.repay.autodebit.model.core.request.AutodebitTriggerType;
import uz.repay.autodebit.model.core.request.AutoPaymentSwitcherDTO;
import uz.repay.autodebit.model.core.response.AutoPayResultV2;
import uz.repay.autodebit.model.core.response.LoanProcessResult;
import uz.repay.autodebit.model.core.response.WorkersResponseDTO;
import uz.repay.autodebit.model.entity.DebitAttemptStatus;
import uz.repay.autodebit.model.property.WorkerProperty;
import uz.repay.autodebit.repository.DebitAttemptRepository;
import uz.repay.autodebit.service.business.AutoPaymentServiceV2;
import uz.repay.autodebit.service.business.AutoPaymentStateControlService;
import uz.repay.autodebit.service.core.LoanResultService;
import uz.repay.autodebit.service.web.CardFeignService;
import uz.repay.autodebit.service.web.LoanFeignService;
import uz.repay.autodebit.service.web.PaymentFeignService;
import uz.repay.autodebit.utils.DebtorsUtils;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.Branch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AutoPaymentServiceV2Impl implements AutoPaymentServiceV2 {

    private static final String MANUAL_CYCLE_PREFIX = "manual-";

    private final WorkerProperty workerProperty;
    private final LoanFeignService loanFeignService;
    private final CopyOnWriteArrayList<PaymentWorkerV2> workers = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<PaymentWorkerV2> insolventWorkers = new CopyOnWriteArrayList<>();
    private final CardFeignService cardFeignService;
    private final PaymentFeignService paymentFeignService;
    private final AutoPaymentStateControlService autoPaymentStateControlService;

    private final MonitoringService monitoringService;
    private final LoanResultService loanResultService;
    private final DebitAttemptRepository debitAttemptRepository;

    @Override
    public boolean processLoan(String loanExternalId, String branchId) {
        return processLoanWithResult(loanExternalId, branchId, AutodebitTriggerType.SCHEDULED).isSuccess();
    }

    @Override
    public LoanProcessResult processLoanWithResult(String loanExternalId, String branchId, AutodebitTriggerType triggerType) {
        if (loanExternalId == null || loanExternalId.isBlank()) {
            return LoanProcessResult.FAILED;
        }

        var manualCycleId = MANUAL_CYCLE_PREFIX + UUID.randomUUID();
        try {
            if (debitAttemptRepository.claim(loanExternalId, manualCycleId, branchId) == 0) {
                log.info("event=autodebit.manual.lock_skipped loanExtId={} trigger={}", loanExternalId, triggerType);
                return LoanProcessResult.SKIPPED_LOCKED;
            }
        } catch (RuntimeException error) {
            log.error("event=autodebit.manual.claim_failed loanExtId={} trigger={}", loanExternalId, triggerType, error);
            return LoanProcessResult.FAILED;
        }

        try {
            var result = processLoanInternal(loanExternalId, branchId);
            debitAttemptRepository.markStatus(loanExternalId, manualCycleId, toAttemptStatus(result).name(), null);
            return result;
        } catch (RuntimeException error) {
            log.error("event=autodebit.manual.execution_failed loanExtId={} trigger={}", loanExternalId, triggerType, error);
            debitAttemptRepository.markStatus(loanExternalId, manualCycleId,
                    DebitAttemptStatus.FAILED_UNKNOWN.name(), "UNEXPECTED: " + error.getMessage());
            return LoanProcessResult.FAILED;
        }
    }

    @Override
    public LoanProcessResult processLoanDirect(String loanExternalId, String branchId) {
        return processLoanInternal(loanExternalId, branchId);
    }

    private LoanProcessResult processLoanInternal(String loanExternalId, String branchId) {
        if (!isAutoPaymentActive()) {
            log.error("Auto payment is disabled. Skipping loanExternalId: {}", loanExternalId);
            return LoanProcessResult.SKIPPED_INACTIVE;
        }

        if (loanExternalId == null || loanExternalId.isBlank()) {
            log.error("Loan external id is empty and will not be processed");
            return LoanProcessResult.FAILED;
        }

        if (branchId == null || branchId.isBlank()) {
            log.error("Branch id is empty and loan will not be processed. loanExternalId: {}", loanExternalId);
            return LoanProcessResult.FAILED;
        }

        var workerId = UUID.randomUUID();
        var result = new AutoPayResultV2(branchId);
        var starter = new DebitStarterService(
                branchId,
                workerProperty != null ? workerProperty.getMinimumDebitAmount() : null,
                isAutoPaymentActive(),
                List.of(loanExternalId),
                cardFeignService,
                loanFeignService,
                paymentFeignService,
                workerId,
                false,
                loanResultService,
                true,
                result,
                UUID.randomUUID(),
                false,
                autoPaymentStateControlService::isActive
        );

        starter.run();

        if (result.getSuccessList().containsKey(loanExternalId)) {
            return LoanProcessResult.SUCCESS;
        }
        // FAILED_UNKNOWN routes the loan through the re-debit cooldown - without
        // this a timed-out (but possibly executed) payment is retried next cycle.
        return result.getUnknownOutcomeList().containsKey(loanExternalId)
                ? LoanProcessResult.FAILED_UNKNOWN
                : LoanProcessResult.FAILED;
    }

    private DebitAttemptStatus toAttemptStatus(LoanProcessResult result) {
        return switch (result != null ? result : LoanProcessResult.FAILED) {
            case SUCCESS -> DebitAttemptStatus.COMPLETED;
            case FAILED_UNKNOWN -> DebitAttemptStatus.FAILED_UNKNOWN;
            case SKIPPED_INACTIVE -> DebitAttemptStatus.SKIPPED_INACTIVE;
            default -> DebitAttemptStatus.FAILED;
        };
    }

    private void addWorkerToGroup(Branch k, List<String> loanList, boolean isInsolvent) {
        if (isInsolvent)
            insolventWorkers
                    .add(
                            new PaymentWorkerV2(
                                    k.getCode(),
                                    workerProperty,
                                    isAutoPaymentActive(),
                                    loanList,
                                    cardFeignService,
                                    loanFeignService,
                                    paymentFeignService,
                                    UUID.randomUUID(),
                                    loanResultService,
                                    isInsolvent,
                                    autoPaymentStateControlService::isActive
                            )
                    );
        else
            workers
                    .add(
                            new PaymentWorkerV2(
                                    k.getCode(),
                                    workerProperty,
                                    isAutoPaymentActive(),
                                    loanList,
                                    cardFeignService,
                                    loanFeignService,
                                    paymentFeignService,
                                    UUID.randomUUID(),
                                    loanResultService,
                                    isInsolvent,
                                    autoPaymentStateControlService::isActive
                            )
                    );
    }


    @Override
    public void switchAP(AutoPaymentSwitcherDTO dto) {
        var enable = Boolean.TRUE.equals(dto.getEnable());
        var active = isAutoPaymentActive();

        if (enable && !active) {
            log.info("Auto Debit starting...");
            start();
        } else if (enable) {
            log.info("Auto payment already working");
        } else if (!active) {
            log.info("Auto payment already stopped");
        } else {
            log.info("Auto payment stopping");
            turnOff();
        }

    }

    @Override
    public ResultData<Boolean> getStatus() {
        log.info("Get auto payment status...");

        return ResultData.ok(
                isAutoPaymentActive()
        );
    }

    @Override
    public ResultData<List<WorkersResponseDTO>> getWorkers() {
        return ResultData.ok(
                Stream.concat(workers.stream(), insolventWorkers.stream())
                        .map(WorkersResponseDTO::new)
                        .toList()
        );
    }

    public void turnOff() {
        autoPaymentStateControlService.setActive(false);

        if (!workers.isEmpty() || !insolventWorkers.isEmpty()) {
            stopWorkers();
            workers.clear();
            insolventWorkers.clear();
        }

    }

    private void stopWorkers() {
        log.info("Got Command to stop v2 workers");
        workers.forEach(PaymentWorkerV2::stopWorker);
        insolventWorkers.forEach(PaymentWorkerV2::stopWorker);

        cleanWorkers(workers);
        cleanWorkers(insolventWorkers);
    }

    private void cleanWorkers(CopyOnWriteArrayList<PaymentWorkerV2> workers) {
        if (!workers.isEmpty()) {
            workers.forEach(z -> {
                if (z.isActive())
                    z.setActive(false);
                z.setFinished(true);
            });
            workers.clear();
        }
    }

    public void start() {
        autoPaymentStateControlService.setActive(true);

        cleanWorkers(workers);
        cleanWorkers(insolventWorkers);

        startInsolventAutoPayment();
    }

    @Scheduled(fixedDelayString = "${internal.worker.sleepForInsolvent}")
    @SchedulerLock(name = "startInsolventAutoPayment", lockAtMostFor = "PT5M", lockAtLeastFor = "PT4M")
    public void startInsolventAutoPayment() {
        if (!isAutoPaymentActive()) {
            log.error("Auto payment is disabled. For continue insolvent payments, Please turn on firstly!");
            return;
        }

        log.info("Starting insolvent auto payment!");
        var loansList = loanFeignService.getInsolventLoans();

        if (loansList.getError() != null) {
            log.error("Insolvent loans list fetching has error: {}", loansList.getError().getMessage());
            return;
        }

        var separatedList = DebtorsUtils.separateListV2ForEachBranch(loansList.getResult());
        log.info("insolvent subLists count: {}", separatedList.size());

        separatedList
                .forEach(
                        (k, v) -> {
                            Optional<PaymentWorkerV2> searchWorkerOptional = insolventWorkers.stream()
                                    .filter(s -> s.getBranch().equals(k.getCode()) && s.isInsolvent())
                                    .findFirst();

                            if (searchWorkerOptional.isEmpty()) {
                                log.info("Worker not found and will be created for : {}", k.getCode());
                                addWorkerToGroup(k, v, true);
                            } else {
                                var paymentWorkerV2 = searchWorkerOptional.get();

                                if (paymentWorkerV2.isFinished()) {
                                    log.info("Found finished worker and will be removed: {}", paymentWorkerV2.getBranch());
                                    insolventWorkers.remove(paymentWorkerV2);
                                    addWorkerToGroup(k, v, true);
                                } else {
                                    log.error("Please a wait. In process already has worker for branch: {}", k.getCode());
                                }
                            }
                        }
                );

        log.info("insolvent workers size: {}", insolventWorkers.size());

        insolventWorkers
                .forEach(
                        z -> {
                            if (!z.isStarted())
                                monitoringService.runProcess(z, true);
                        }
                );


    }

    private boolean isAutoPaymentActive() {
        return autoPaymentStateControlService.isActive();
    }
}
