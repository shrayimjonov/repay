package uz.repay.autodebit.service.business.impl;

import feign.FeignException;
import feign.RetryableException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uz.repay.autodebit.model.loan.response.DebtLoansResult;
import uz.repay.autodebit.model.property.WorkerProperty;
import uz.repay.autodebit.service.business.AutoPaymentService;
import uz.repay.autodebit.service.core.PayResultService;
import uz.repay.autodebit.service.web.CardFeignService;
import uz.repay.autodebit.service.web.LoanFeignService;
import uz.repay.autodebit.service.web.PaymentFeignService;
import uz.repay.autodebit.utils.DebtorsUtils;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.ResultState;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoPaymentServiceImpl implements AutoPaymentService {
    private final WorkerProperty workerProperty;

    private final LoanFeignService loanFeignService;
    private final List<PaymentWorker> workers = new ArrayList<>();
    private final CardFeignService cardFeignService;
    private final PaymentFeignService paymentFeignService;
    private final PayResultService payResultService;
    private volatile boolean isActive = true;

    // @PostConstruct
    public void doAP() {
        var debtorsResponse = getDebtorsList();
        log.info("Starting auto payment!");

        if (debtorsResponse.getError() != null) {
            log.error("Get debtors list has error: {}", debtorsResponse.getError().getMessage());
            return;
        }

        log.info("Debtors list size: {}", debtorsResponse.getResult().size());

        if (debtorsResponse.getResult().isEmpty()) {
            log.warn("Debtors list is empty and will be exited!");
            return;
        }

        var separatedList = DebtorsUtils.separateListForEachBranch(debtorsResponse.getResult());

        separatedList
                .forEach((k, v) ->
                        workers.add(
                                new PaymentWorker(
                                        k.getCode(),
                                        workerProperty.getMinimumDebitAmount(),
                                        isActive,
                                        v,
                                        cardFeignService,
                                        loanFeignService,
                                        paymentFeignService
                                )
                        ));

        workers
                .forEach(
                        this::runProcess
                );


    }

    private void runProcess(PaymentWorker z) {

        CompletableFuture
                .supplyAsync(
                        z::call
                )
                .thenApply(s -> {
                            stopWorkerByBranch(z.getBranch());

                            log.info("""

                                             Success payments: {}
                                             Failed payments: {}
                                            """,
                                    s.getSuccessList().size(),
                                    s.getFailedList().size());

                            payResultService.create(s);

                            return s;
                        }
                )
                .thenAccept(d -> {
                    if (isActive) {
                        var pw = new PaymentWorker(
                                d.getBranch(),
                                workerProperty.getMinimumDebitAmount(),
                                isActive,
                                getDebtorsByBranch(d.getBranch()),
                                cardFeignService,
                                loanFeignService,
                                paymentFeignService
                        );
                        workers.add(pw);
                        runProcess(pw);
                    }
                });
    }

    // @Scheduled(fixedDelay = 100_000) // turn on automatically
    public void startFinishedWorkersAgain() {
        Arrays.stream(Branch.values())
                .forEach(b ->
                        {
                            if (workers.stream().noneMatch(branchEqualsToRequestedBranch(b.getCode())))
                                if (isActive)
                                    runProcess(
                                            new PaymentWorker(
                                                    b.getCode(),
                                                    workerProperty.getMinimumDebitAmount(),
                                                    isActive,
                                                    getDebtorsByBranch(b.getCode()),
                                                    cardFeignService,
                                                    loanFeignService,
                                                    paymentFeignService
                                            ));
                        }
                );
    }

    private Predicate<PaymentWorker> branchEqualsToRequestedBranch(String branch) {
        return b -> b.getBranch().equals(branch);
    }

    public List<DebtLoansResult> getDebtorsByBranch(String branch) {
        var debtorsListResponse =
                getDebtorsList();

        if (debtorsListResponse.getError() != null) {
            log.error("takeThisBranchDebtors method has error: {}", debtorsListResponse.getError().getMessage());
            return List.of();
        }

        return debtorsListResponse
                .getResult()
                .stream()
                .filter(takeThisBranchDebtors(branch))
                .toList();
    }

    private Predicate<DebtLoansResult> takeThisBranchDebtors(String branch) {
        return debtors -> debtors
                .getBranch().equals(branch);
    }


    private ResultData<List<DebtLoansResult>> getDebtorsList() {
        try {
            return loanFeignService.getAllDebtLoans();
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }

    @Override
    public ResultData<String> stop() {
        turnOff();
        return new ResultData<>("Auto payment stopped");
    }

    private void stopWorkers() {
        log.info("Got Command to stop workers");
        workers.forEach(PaymentWorker::stopWorker);
    }

    private void stopWorkerByBranch(String branch) {
        log.info("Stop branch: {}", branch);

        workers
                .stream()
                .filter(z -> z.getBranch().equals(branch))
                .findFirst()
                .ifPresent(w -> {
                            if (w.isActive())
                                w.stopWorker();
                            workers.remove(w);
                        }
                );
    }

    public void turnOff() {
        stopWorkers();
        isActive = false;
        workers.clear();
    }

}
