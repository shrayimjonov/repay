package uz.nodir.notification.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.notification.client.LoanFeignClient;
import uz.nodir.notification.service.web.LoanClientService;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanClientServiceImpl implements LoanClientService {
    private final LoanFeignClient client;

    @Override
    public SimpleResponse<ResultData<List<String>>> takeLoanFeaturedCreators(String loanExtId) {
        log.info("Send to Loan Feign Client take loan featured creators request by: {}", loanExtId);

        try {
            var response = client.takeLoanFeaturedCreators(
                    loanExtId
            );
            log.info("Received Loan Feign client take loan featured creators response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Loan Feign client take loan featured creators method throws error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("Loan Feign client take loan featured creators method throws error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("Loan Feign client take loan featured creators method throws error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<LoanInfoResponse>> getLoanBalance(LoanSimpleRequestDTO requestDTO) {
        log.info("Send to Loan Feign Client to get loan balance request by: {}", requestDTO);

        try {
            var response = client.getLoanBalance(
                    requestDTO
            );
            log.info("Received Loan Feign client to get loan balance response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Loan Feign client to get loan balance method throws error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("Loan Feign client to get loan balance method throws error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("Loan Feign client to get loan balance method throws error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }



}
