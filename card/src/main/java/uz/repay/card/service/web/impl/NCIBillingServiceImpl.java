package uz.repay.card.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.client.NCIBillingFeignClient;
import uz.repay.card.model.dto.nciBilling.request.SimpleLoanRequest;
import uz.repay.card.model.dto.nciBilling.response.NCICardsByPersonCodeResponse;
import uz.repay.card.model.dto.nciBilling.response.NCIClientInfoByPersonCode;
import uz.repay.card.service.web.NCIBillingService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.enums.ResultState;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NCIBillingServiceImpl implements NCIBillingService {
    private final NCIBillingFeignClient client;


    @Override
    public ResultData<List<NCICardsByPersonCodeResponse>> getCardsByPersonCode(String personCode) {
        log.info("Get cards by person code: {} from nci_billing", personCode);
        try {
            var response = client.getCardsByPersonCode(personCode);
            log.info("Cards by person code response: {}", response);
            return response;
        } catch (RetryableException r) {
            log.error("NCI_BILLING cards by person code method has TIME OUT error: {}", r.getMessage());
            return new ResultData<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("NCI_BILLING cards by person code method has FEIGN error: {}", f.getMessage());
            return new ResultData<>(581, f.getMessage());
        } catch (Exception e) {
            log.error("NCI_BILLING cards by person code method has error: {}", e.getMessage());
            return new ResultData<>(582, e.getMessage());
        }
    }

    @Override
    public ResultData<LoanInfoResponse> getLoanInfo(String loanExtId) {
        log.info("Get loan: {} info from nci_billing", loanExtId);
        var request = new SimpleLoanRequest(
                loanExtId
        );
        try {
            var response = client.getLoanInfo(request);
            log.info("Loan info response: {}", response);
            return response;
        } catch (RetryableException r) {
            log.error("NCI_BILLING Loan info method has TIME OUT error: {}", r.getMessage());
            return new ResultData<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("NCI_BILLING Loan info method has FEIGN error: {}", f.getMessage());
            return new ResultData<>(581, f.getMessage());
        } catch (Exception e) {
            log.error("NCI_BILLING Loan info method has error: {}", e.getMessage());
            return new ResultData<>(582, e.getMessage());
        }
    }



    @Override
    public SimpleResponse<ResultData<List<NCIClientInfoByPersonCode>>> getClientInfoByPersonCode(String personCode) {
        log.info("Sending request to NCI_BILLING for take getClientInfoByPersonCode by person_code: {}", personCode);
        try {
            var response = client.getClientInfoByPersonCode(personCode);
            log.info("Received response from NCI_BILLING by take getClientInfoByPersonCode method with result: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Taken a Time-OUT error while executing getClientInfoByPersonCode method with message: {}", r.getMessage());
            return new SimpleResponse<>(r.getMessage(), false);
        } catch (FeignException f) {
            log.error("Taken a ThirdParty error while executing getClientInfoByPersonCode method with message: {}", f.getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), f.getMessage());
        } catch (Exception e) {
            log.error("Taken an internal error while executing getClientInfoByPersonCode method with message: {}", e.getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }
}
