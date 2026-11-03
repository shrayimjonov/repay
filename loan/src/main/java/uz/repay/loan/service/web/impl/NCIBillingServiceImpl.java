package uz.repay.loan.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.enums.ResultState;
import uz.repay.loan.client.NCIBillingFeignClient;
import uz.repay.loan.model.dto.nciBilling.request.LoanRepayRequest;
import uz.repay.loan.model.dto.nciBilling.request.LoanTranRequest;
import uz.repay.loan.model.dto.nciBilling.request.SimpleBranchRequest;
import uz.repay.loan.model.dto.nciBilling.request.SimpleLoanRequest;
import uz.repay.loan.model.dto.nciBilling.response.ClientInfoResponse;
import uz.repay.loan.model.dto.nciBilling.response.DebtsListResponse;
import uz.repay.loan.model.dto.nciBilling.response.ProblemResponse;
import uz.repay.loan.model.dto.nciBilling.response.TransactionResponse;
import uz.repay.loan.service.web.NCIBillingService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NCIBillingServiceImpl implements NCIBillingService {
    private final NCIBillingFeignClient client;

    @Override
    @Deprecated
    public ResultData<LoanInfoResponse> getLoanInfoByLoanId(String loanId) {
        var request = new SimpleLoanRequest(loanId);
        log.info("getLoanInfoByLoanId request: {}", request);
        try {
            var response = client.getLoanInfoByLoanId(request);
            log.info("getLoanInfoByLoanId Response: {}", response);
            return response;
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }

    @Override
    @Deprecated
    public ResultData<ClientInfoResponse> getClientInfoByLoanId(String loanId) {
        var request = new SimpleLoanRequest(loanId);
        log.info("getClientInfoByLoanId request: {}", request);
        try {
            var response = client.getClientInfoByLoanId(request);
            log.info("getClientInfoByLoanId Response: {}", response);
            return response;
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }


    @Override
    @Retryable(
            retryFor = {
                    RetryableException.class,
                    FeignException.class
            },
            maxAttempts = 5,
            backoff = @Backoff(delay = 30000)
    )
    @Deprecated
    public ResultData<DebtsListResponse> getDebtorsListByBranch(String branch) {
        var request = new SimpleBranchRequest(branch);
        log.info("getDebtorsListByBranch request: {}", request);
        return client.getDebtorsListByBranch(request);
    }


    @Override
    @Deprecated
    public ResultData<List<TransactionResponse>> getLoanTransactions(String loanId,
                                                                     String dateFrom) {
        var request = new LoanTranRequest(loanId, dateFrom);
        log.info("getLoanTransactions request: {}", request);
        try {
            var response = client.getLoanTransactions(request);
            log.info("getLoanTransactions Response: {}", response);
            return response;
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }

    @Override
    public ResultData<ProblemResponse> loanRepay(String loanId, Long amount) {
        var request = new LoanRepayRequest(loanId, amount);
        log.info("loanRepay request: {}", request);
        try {
            var response = client.repayV2(request);
            log.info("loanRepay Response: {}", response);
            return new ResultData<>(response);
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }


}
