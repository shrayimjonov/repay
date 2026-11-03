package uz.repay.loan.service.core.component;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.ResultState;
import uz.repay.loan.model.dto.nciBilling.response.DebtsListResponse;
import uz.repay.loan.model.entity.DebtLoan;
import uz.repay.loan.model.entity.Loan;
import uz.repay.loan.mongo.entity.DebtLoanDailyStats;
import uz.repay.loan.service.business.LoanModifyService;
import uz.repay.loan.service.core.DebtLoanDAO;
import uz.repay.loan.service.core.DebtLoanDailyStatsDAO;
import uz.repay.loan.service.core.LoanDAO;
import uz.repay.loan.service.web.NCIGatewayClientService;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DebtLoanUpdaterComponent {
    private final DebtLoanDAO debtLoanDAO;
    private final NCIGatewayClientService nciGatewayClientService;
    private final LoanDAO loanDAO;
    private final LoanModifyService loanModifyService;
    private final DebtLoanDailyStatsDAO debtLoanDailyStatsDAO;

    @Async
    public synchronized void refreshDebtLoanList() {
        log.info("Starting refresh Debt Loans from ABS...");

        var branchesDebtsList = getDebtorsFromABS();
        log.info("Branches debts list size: {}", branchesDebtsList.size());

        if (branchesDebtsList.isEmpty()) {
            log.info("Branches Debt List is empty!");
            return;
        }

        log.info("Get all not Deleted loans <<*>>");
        Map<String, Loan> loansByExtId = loanDAO.findAll()
                .stream()
                .collect(Collectors.toMap(Loan::getExtId, Function.identity(), (a, b) -> a));

        var shouldBeRegisterLoans = new ConcurrentLinkedQueue<String>();
        var newDebtLoansList = new ConcurrentLinkedQueue<DebtLoan>();

        int total = branchesDebtsList.size();
        var processed = new AtomicInteger(0);

        int concurrency = 50;
        var semaphore = new Semaphore(concurrency);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (var l : branchesDebtsList) {
                executor.submit(() -> {
                    try {
                        semaphore.acquire();
                        try {
                            handleDebtLoan(l, loansByExtId, shouldBeRegisterLoans, newDebtLoansList);
                        } finally {
                            semaphore.release();
                            log.info("Debtor list size is {} and currently processing {}'s debit",
                                    total, processed.incrementAndGet());
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }

        log.info("Before saving. total: {}, processed: {}", total, processed);
        debtLoanDAO.replaceAll(new ArrayList<>(newDebtLoansList));

        log.info("Register not found loans. Count -> {}", shouldBeRegisterLoans.size());
        shouldBeRegisterLoans
                .forEach(s -> loanModifyService.addWithoutBranchValidation(new LoanSimpleRequestDTO(s)));

        log.info("Refreshing Debt Loans is finished :)");
    }

    private void handleDebtLoan(DebtsListResponse.Info l,
                                Map<String, Loan> loansByExtId,
                                ConcurrentLinkedQueue<String> shouldBeRegisterLoans,
                                ConcurrentLinkedQueue<DebtLoan> newDebtLoansList) {
        if (Arrays.stream(Branch.values()).noneMatch(b -> b.getCode().equals(l.getBranch()))) {
            log.error("Branch: {} not defined", l.getBranch());
            return;
        }

        var loanExtId = l.getBranch() + l.getLoanId();
        var loan = loansByExtId.get(loanExtId);

        if (loan == null) {
            log.info("Loan: {} not found and will be created", loanExtId);
            shouldBeRegisterLoans.add(loanExtId);
            return;
        }

        if (loan.getIsDeleted()) {
            log.error("Loan: {} is deleted and won't be registered in debt loan", loan.getExtId());
            return;
        }

        if (!loan.getIsAutoDebit()) {
            log.error("Loan: {} is auto-debit disabled and won't be registered in debt loan", loan.getExtId());
            return;
        }

        ResultData<LoanInfoResponse> absResponse;
        try {
            absResponse = nciGatewayClientService.getLoanInfoByLoanIdWithRetry(loanExtId);
        } catch (Exception e) {
            log.error("For loan {} ABS call failed after retries: {}", loanExtId, e.getMessage());
            return;
        }

        if (absResponse == null || absResponse.getError() != null || absResponse.getResult() == null) {
            log.error("For loan {} process can't continue. ABS response: {}", loanExtId, absResponse);
            return;
        }

        if (!Objects.equals(absResponse.getResult().getCurrency(), "000")) {
            log.warn("Loan: {} is not national currency! Can't add to debtors list...", loanExtId);
            return;
        }

        log.info("Debt Loan prepare entity for loan: {}", loanExtId);
        var debtLoan = new DebtLoan(loan, l.getRecommendedAmount(), l.getMaxAmount());
        newDebtLoansList.add(debtLoan);
    }

    private List<DebtsListResponse.Info> getDebtorsFromABS() {
        var branchesDebtsList = new ArrayList<DebtsListResponse.Info>();
        Arrays.stream(Branch.values())
                .forEach(b -> {
                    log.info("Get debtors for branch: {}", b.getCode());
                    var response = getDebtsListResponse(b);
                    if (response.getStatus()) {
                        branchesDebtsList.addAll(response.getData().getInfo());
                    } else {
                        log.error("Debt result has error: {}", response.getError().getMessage());
                    }
                });
        return branchesDebtsList;
    }

    private SimpleResponse<DebtsListResponse> getDebtsListResponse(Branch b) {
        try {
            return nciGatewayClientService.getDebtorsListByBranch(b.getCode());
        } catch (RetryableException r) {
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }

    public synchronized void saveDailyDebtLoanReport() {
        log.info("Start saving DailyDebtLoanReport...");

        var debtLoans = debtLoanDAO.findDailyReport();

        if (debtLoans.isEmpty()) {
            log.info("No daily debtors found");
            return;
        }

        for (var debtLoan : debtLoans) {

            var debtLoanStatsResult = debtLoanDailyStatsDAO.findStatsByDateAndBranch(debtLoan.getCreated_date(), debtLoan.getBranch());

            if (debtLoanStatsResult.isEmpty()) {
                log.info("Debt Loan Stats for branch: {} not found and will be created", debtLoan.getBranch());

                var debtLoanStat = new DebtLoanDailyStats(debtLoan);
                debtLoanDailyStatsDAO.save(debtLoanStat);
                continue;
            }

            log.warn("Debt loan stats for branch {} and to date: {} already exist! ", debtLoan.getBranch(), debtLoan.getCreated_date());
        }

        log.info("Finished saving DailyDebtLoanReport");
    }
}
