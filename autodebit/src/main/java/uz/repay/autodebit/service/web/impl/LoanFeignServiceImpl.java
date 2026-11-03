package uz.repay.autodebit.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import uz.repay.autodebit.client.LoanFeignClient;
import uz.repay.autodebit.model.loan.response.DebtLoansPageResponseDTO;
import uz.repay.autodebit.model.loan.response.DebtLoansResult;
import uz.repay.autodebit.service.web.LoanFeignService;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoV3Response;
import uz.repay.common.model.dto.response.loan.LoanShortV2ResponseDTO;
import uz.repay.common.model.enums.ResultState;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanFeignServiceImpl implements LoanFeignService {
    private final LoanFeignClient client;

    @Override
    @Retryable(
            retryFor = {
                    RetryableException.class,
                    FeignException.class
            },
            maxAttempts = 5,
            backoff = @Backoff(delay = 30000)
    )
    public ResultData<List<DebtLoansResult>> getAllDebtLoans() {
        log.info("Get all debt loans list");
        return client.getDebtLoans();
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
    public ResultData<LoanInfoResponse> getLoanBalance(String loanExtId) {
        log.info("Get loan: {} balance info", loanExtId);
        var response = client.getLoanBalance(new LoanSimpleRequestDTO(loanExtId));
        log.info("Balance response: {}", response);
        return response;
    }


    @Override
    public SimpleResponse<ResultData<Boolean>> removeDebtLoanById(Long id) {
        log.info("Remove debt loan by id: {}", id);
        try {
            var response = client.removeFromDebtLoan(id);
            log.info("Remove debt loan response: {}", response);

            return new SimpleResponse<>(response);
        } catch (Exception e) {
            log.error("Remove debt loan throws error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    @Override
    public ResultData<LoanInfoV3Response> getLoanBalanceV3(String loanExtId) {
        var request = new LoanSimpleRequestDTO(loanExtId);
        log.info("Sending get loan balance v3 request to LOAN SERVICE: {}", request);
        try {
            var response = client.getLoanBalanceV3(request);
            log.info("Received get loan balance v3 response from LOAN SERVICE: {}", response);
            return response;
        } catch (RetryableException r) {
            log.error("Received time out error from to get loan balance v3 method with message: {}", r.getMessage());
            return new ResultData<>(ResultState.NotProcessed, r.getMessage());
        } catch (Exception e) {
            log.error("Received internal error from to get loan balance v3 method with message: {}", e.getMessage());
            return new ResultData<>(ResultState.BlockOperation, e.getMessage());
        }
    }

    @Override
    public ResultData<List<LoanShortV2ResponseDTO>> getInsolventLoans() {
        log.info("Sending get insolvent loans request to LOAN SERVICE");
        try {
            var response = client.getInsolventLoans();
            log.info("Received get insolvent loans response from LOAN SERVICE: {}", response);
            return response;
        } catch (RetryableException r) {
            log.error("Received time out error from to get insolvent loans method with message: {}", r.getMessage());
            return new ResultData<>(ResultState.NotProcessed, r.getMessage());
        } catch (Exception e) {
            log.error("Received internal error from to get insolvent loans method with message: {}", e.getMessage());
            return new ResultData<>(ResultState.BlockOperation, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<Boolean>> removeDebtLoanByLoanExtId(String loanExtId) {
        log.info("Sending request to LOAN SERVICE to remove debt loan by loanExtId: {}", loanExtId);
        try {
            var response = client.removeFromDebtLoanByLoanExtId(loanExtId);
            log.info("Received response from LOAN SERVICE about remove debt loan with response: {}", response);
            return new SimpleResponse<>(ResultData.ok(response.getIsSuccess()));
        } catch (Exception e) {
            log.error("Taken an error from LOAN SERVICE while executing remove debt loan method throws error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    @Override
    public ResultData<LoanAdditionalResponseDTO> getLoanAdditionalInfo(String loanExtId) {
        var request = new LoanSimpleRequestDTO(loanExtId);
        log.debug("Sending request to LOAN SERVICE to get loan additional info with request: {}", request);

        try {
            var response = client.getLoanAdditionalInfo(request);
            log.debug("Received response from LOAN SERVICE about loan addition info with response: {}", response);
            return response;
        } catch (RetryableException r) {
            log.error("Time out from LOAN SERVICE while executing loan additional info: {}", r.getMessage());
            return new ResultData<>(504, r.getMessage());
        } catch (Exception e) {
            log.error("Taken an error from LOAN SERVICE while executing loan addition info method throws error: {}", e.getMessage());
            return new ResultData<>(500, e.getMessage());
        }
    }

    @Override
    public ResultData<List<DebtLoansResult>> getAllDebtLoans2() {
        log.info("Sending request to Loan Service for get all debt loans list");
        try {
            var response = client.getDebtLoans();
            log.info("Received response from Loan Service for get all debt loans list");
            return response;
        } catch (RetryableException r) {
            log.error("Taken time out error from Loan Service for get all debt loans method with message: {}", r.getMessage());
            return new ResultData<>(ResultState.NotProcessed, r.getMessage());
        } catch (Exception e) {
            log.error("Taken internal server error from Loan Service for get all debt loans method with message: {}", e.getMessage());
            return new ResultData<>(ResultState.BlockOperation, e.getMessage());
        }
    }

    @Override
    public ResultData<byte[]> getAllDebtLoansV2() {
        log.info("Sending request to Loan Service for get all debt loans v2 list");
        try {
            var response = client.getDebtLoansV2();
            log.info("Received response from Loan Service for get all debt loans v2 list");
            return response;
        } catch (RetryableException r) {
            log.error("Taken time out error from Loan Service for get all debt loans v2 method with message: {}", r.getMessage());
            return new ResultData<>(ResultState.NotProcessed, r.getMessage());
        } catch (Exception e) {
            log.error("Taken internal server error from Loan Service for get all debt loans v2 method with message: {}", e.getMessage());
            return new ResultData<>(ResultState.BlockOperation, e.getMessage());
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
    public ResponseEntity<ResultData<DebtLoansPageResponseDTO>> getAllDebtLoansPageV2(int limit, long pageNumber) {
        log.info("Sending request to Loan Service for get debt loans page v2 list with limit: {}, pageNumber: {}", limit, pageNumber);
        try {
            var response = client.getDebtLoansPage(limit, pageNumber);
            log.info("Received response from Loan Service for get debt loans page v2 list");
            return response;
        } catch (RetryableException r) {
            log.error("Taken time out error from Loan Service for get debt loans page v2 method with message: {}", r.getMessage());
            return ResponseEntity.ok(new ResultData<>(ResultState.NotProcessed, r.getMessage()));
        } catch (Exception e) {
            log.error("Taken internal server error from Loan Service for get debt loans page v2 method with message: {}", e.getMessage());
            return ResponseEntity.ok(new ResultData<>(ResultState.BlockOperation, e.getMessage()));
        }
    }
}
