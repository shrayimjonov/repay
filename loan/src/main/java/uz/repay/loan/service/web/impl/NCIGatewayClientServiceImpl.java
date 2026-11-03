package uz.repay.loan.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoV3Response;
import uz.repay.common.model.enums.ResultState;
import uz.repay.loan.client.NCIGatewayFeignClient;
import uz.repay.loan.model.dto.nciBilling.request.LoanTranRequest;
import uz.repay.loan.model.dto.nciBilling.request.SimpleBranchRequest;
import uz.repay.loan.model.dto.nciBilling.response.ClientInfoResponse;
import uz.repay.loan.model.dto.nciBilling.response.DebtsListResponse;
import uz.repay.loan.model.dto.nciBilling.response.TransactionResponse;
import uz.repay.loan.model.dto.nciGateway.request.NCIGatewayClientInfoRequest;
import uz.repay.loan.model.dto.nciGateway.request.NCIGatewayLoanInfoRequest;
import uz.repay.loan.model.dto.nciGateway.response.NCIBankDayResponse;
import uz.repay.loan.model.dto.nciGateway.response.NCILoanInfoRepayResponse;
import uz.repay.loan.service.web.NCIGatewayClientService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NCIGatewayClientServiceImpl implements NCIGatewayClientService {
    private final NCIGatewayFeignClient client;

    @Override
    public SimpleResponse<ResultData<LoanInfoResponse>> getLoanInfoByLoanId(String loanId) {
        var request = new NCIGatewayLoanInfoRequest(loanId);
        log.info("nciGateway getLoanInfoByLoanId request: {}", request);
        try {
            var response = client.getLoanInfoByLoanId(request);
            log.info("nciGateway getLoanInfoByLoanId Response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), "nciGateway TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "nciGateway THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }

    @Override
    @Retryable(
            retryFor = {
                    RetryableException.class,
                    FeignException.class
            },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 15000)
    )
    public ResultData<LoanInfoResponse> getLoanInfoByLoanIdWithRetry(String loanId) {
        var request = new NCIGatewayLoanInfoRequest(loanId);
        log.debug("nciGateway getLoanInfoByLoanIdWithRetry request: {}", request);
        return client.getLoanInfoByLoanId(request);
    }

    @Override
    public SimpleResponse<ClientInfoResponse> getClientInfoByLoanId(String loanId) {
        var request = new NCIGatewayClientInfoRequest(loanId);
        log.info("nciGateway getClientInfoByLoanId request: {}", request);
        try {
            var response = client.getClientInfoByLoanId(request);
            log.info("nciGateway getClientInfoByLoanId Response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), "nciGateway TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "nciGateway THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }


    @Override
    public SimpleResponse<DebtsListResponse> getDebtorsListByBranch(String branch) {
        var request = new SimpleBranchRequest(branch);
        log.info("nciGateway getDebtorsListByBranch request: {}", request);
        try {
            var response = client.getDebtorsListByBranch(request);
            log.info("nciGateway getDebtorsByBranch is received...");
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), "nciGateway TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "nciGateway THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }


    @Override
    public SimpleResponse<List<TransactionResponse>> getLoanTransactions(String loanId,
                                                                         String dateFrom) {
        var request = new LoanTranRequest(loanId, dateFrom);
        log.info("nciGateway getLoanTransactions request: {}", request);
        try {
            var response = client.getLoanTransactions(request);
            log.info("nciGateway getLoanTransactions Response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), "nciGateway TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "nciGateway THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }

    @Override
    public SimpleResponse<List<NCIBankDayResponse>> checkBankDay() {
        log.info("Sending nciGateway check bank day request");
        try {
            var response = client.checkBankDay();
            log.info("Taken nciGateway check bank day response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), "nciGateway TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "nciGateway THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }


    @Override
    public SimpleResponse<ResultData<LoanInfoV3Response>> getLoanInfoV3(String loanId) {
        var request = new NCIGatewayLoanInfoRequest(loanId);
        log.info("Sending get loan info v3 request to nci_gateway: {}", request);
        try {
            var response = client.getLoanInfoV3(request);
            log.info("Received get loan info v3 method response from nci_gateway: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("NCI_GATEWAY get loan info v3 method takes a time out error: {}", r.getMessage());
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), "nciGateway TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            log.error("NCI_GATEWAY get loan info v3 method takes a feign error: {}", f.getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "nciGateway THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            log.error("NCI_GATEWAY get loan info v3 method takes an internal error: {}", e.getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<NCILoanInfoRepayResponse>> getLoanInfoRepayByLoanExtId(String loanExtId) {
        var request = new NCIGatewayLoanInfoRequest(loanExtId);
        log.info("Sending request to nciGateway method \" get loan info repay \"  with request: {}", request);
        try {
            var response = client.getLoanInfoRepay(request);
            log.info("Taken response from nciGateway method \" get loan info repay \"  with response : {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Occurred a TIME OUT error while executing  nciGateway method \" get loan info repay \" with message: {}", r.getMessage());
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), "nciGateway TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            log.error("Occurred a THIRD PARTY error while executing  nciGateway method \" get loan info repay \" with message: {}", f.getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "nciGateway THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            log.error("Occurred an INTERNAL error while executing  nciGateway method \" get loan info repay \" with message: {}", e.getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }

}
