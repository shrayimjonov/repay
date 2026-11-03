package uz.repay.autodebit.service.business.implV2;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import uz.repay.autodebit.model.core.response.AutoPayResultV2;
import uz.repay.autodebit.model.property.WorkerProperty;
import uz.repay.autodebit.service.core.LoanResultService;
import uz.repay.autodebit.service.web.CardFeignService;
import uz.repay.autodebit.service.web.LoanFeignService;
import uz.repay.autodebit.service.web.PaymentFeignService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.function.BooleanSupplier;

@Slf4j
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class PaymentWorkerV2 implements Callable<AutoPayResultV2> {
    private String branch;
    private WorkerProperty workerProperty;
    private volatile boolean isActive;
    private List<String> debtorsList;
    private CardFeignService cardFeignService;
    private LoanFeignService loanFeignService;
    private PaymentFeignService paymentFeignService;
    private UUID workerId;
    private volatile boolean isFinished = false;
    private LoanResultService loanResultService;
    private volatile boolean isStarted = false;
    private final CopyOnWriteArrayList<DebitStarterService> starters = new CopyOnWriteArrayList<>();
    private boolean isInsolvent;
    private BooleanSupplier activeStateSupplier = () -> true;

    public PaymentWorkerV2(String branch,
                           WorkerProperty workerProperty,
                           boolean isActive,
                           List<String> debtorsList,
                           CardFeignService cardFeignService,
                           LoanFeignService loanFeignService,
                           PaymentFeignService paymentFeignService,
                           UUID workerId,
                           LoanResultService loanResultService,
                           boolean isInsolvent) {
        this(branch, workerProperty, isActive, debtorsList, cardFeignService, loanFeignService, paymentFeignService,
                workerId, loanResultService, isInsolvent, () -> true);
    }

    public PaymentWorkerV2(String branch,
                           WorkerProperty workerProperty,
                           boolean isActive,
                           List<String> debtorsList,
                           CardFeignService cardFeignService,
                           LoanFeignService loanFeignService,
                           PaymentFeignService paymentFeignService,
                           UUID workerId,
                           LoanResultService loanResultService,
                           boolean isInsolvent,
                           BooleanSupplier activeStateSupplier) {
        this.isActive = isActive;
        this.branch = branch;
        this.workerProperty = workerProperty;
        this.debtorsList = debtorsList;
        this.cardFeignService = cardFeignService;
        this.loanFeignService = loanFeignService;
        this.paymentFeignService = paymentFeignService;
        this.workerId = workerId;
        this.loanResultService = loanResultService;
        this.isInsolvent = isInsolvent;
        this.activeStateSupplier = activeStateSupplier != null ? activeStateSupplier : () -> true;
    }


    @Override
    public AutoPayResultV2 call() {
        log.info(" ----------------Starting autoPaymentV2! ---------------");
        isStarted = true;
        isFinished = false;
        starters.clear();
        var result = new AutoPayResultV2(branch);
        result.setSuccessList(new ConcurrentHashMap<>());
        result.setFailedList(new ConcurrentHashMap<>());
        log.info("Start {} session for: {}", result.getSessionID(), branch);


        if (debtorsList == null || debtorsList.isEmpty()) {
            log.info("Debtors list empty and Worker {} is finished", Thread.currentThread().getName());
            isFinished = true;
            return result;
        }

        if (!isAutoPaymentActive()) {
            log.info("Auto payment is disabled and Worker {} is finished", Thread.currentThread().getName());
            isFinished = true;
            return result;
        }

        log.info("{} Debtors all list for branch: {}", branch, debtorsList);

        var partitionSize = resolvePartitionSize();
        var separatedDebtors = Lists.partition(debtorsList, partitionSize);
        log.info("Branch: {} separated list size: {}", branch, separatedDebtors.size());

        try {
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                var starterFutures = separatedDebtors.stream()
                        .map(debtorsSubList -> createStarter(debtorsSubList, result))
                        .map(starter -> CompletableFuture.runAsync(starter, executor))
                        .toArray(CompletableFuture<?>[]::new);

                CompletableFuture.allOf(starterFutures).join();
            }
        } catch (CompletionException error) {
            var cause = error.getCause() != null ? error.getCause() : error;
            log.error("Branch: {} session {} failed", branch, result.getSessionID(), cause);
            throw cause instanceof RuntimeException runtimeException
                    ? runtimeException
                    : new RuntimeException(cause);
        } finally {
            isFinished = true;
            log.info("Branch: {} session {} is finished", branch, result.getSessionID());
        }

        if (Boolean.TRUE.equals(result.getEmergencyExit())) {
            log.info("Auto payment stopped by emergence exit signal!");
            isActive = false;
        }

        return result;
    }

    private int resolvePartitionSize() {
        if (workerProperty == null || workerProperty.getSubListSize() == null || workerProperty.getSubListSize() <= 0) {
            return Math.max(1, debtorsList.size());
        }

        return workerProperty.getSubListSize();
    }

    private DebitStarterService createStarter(List<String> debtorsSubList, AutoPayResultV2 result) {
        var threadId = UUID.randomUUID();
        var starter = new DebitStarterService(
                branch,
                workerProperty != null ? workerProperty.getMinimumDebitAmount() : null,
                isActive,
                debtorsSubList,
                cardFeignService,
                loanFeignService,
                paymentFeignService,
                workerId,
                isFinished,
                loanResultService,
                isStarted,
                result,
                threadId,
                isInsolvent,
                activeStateSupplier
        );

        starters.add(starter);
        log.info("Start new debit service for branch : {} with id: {}", branch, threadId);
        return starter;
    }


    public void stopWorker() {
        isActive = false;
        starters.forEach(s -> {
                    s.setActive(isActive);
                    s.setFinished(!isActive);
                }
        );
        isFinished = true;
    }

    private boolean isAutoPaymentActive() {
        return isActive && activeStateSupplier.getAsBoolean();
    }

}
