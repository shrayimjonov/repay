package uz.repay.payment.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.client.VisaIntegrationFeignClient;
import uz.repay.payment.model.dto.visa.request.VisaHistoryRequest;
import uz.repay.payment.model.dto.visa.request.VisaTranBaseRequest;
import uz.repay.payment.model.dto.visa.response.VisaHistoryResponse;
import uz.repay.payment.model.dto.visa.response.VisaTranBaseResponse;
import uz.repay.payment.service.web.VisaIntegrationService;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisaIntegrationServiceImpl implements VisaIntegrationService {
    private final VisaIntegrationFeignClient client;

    @Override
    public SimpleResponse<ResultData<VisaTranBaseResponse>> debit(VisaTranBaseRequest request) {
        log.info("Visa integration debit request: {}", request);
        try {
            var response = client.debit(request);
            log.info("Visa integration debit response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Visa integration debit throws TIME OUT error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("Visa integration debit throws FEIGN error: {}", f.getMessage());
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            log.error("Visa integration debit throws error: {}", e.getMessage());
            return new SimpleResponse<>(582, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<VisaTranBaseResponse>> reverse(VisaTranBaseRequest request) {
        log.info("Visa integration reverse request: {}", request);
        try {
            var response = client.reverse(request);
            log.info("Visa integration reverse response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Visa integration reverse throws TIME OUT error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("Visa integration reverse throws FEIGN error: {}", f.getMessage());
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            log.error("Visa integration reverse throws error: {}", e.getMessage());
            return new SimpleResponse<>(582, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<VisaHistoryResponse> history(VisaHistoryRequest request) {
        log.info("Visa integration history request: {}", request);
        try {
            var response = client.history(request);
            log.info("Visa integration history response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Visa integration history throws TIME OUT error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("Visa integration history throws FEIGN error: {}", f.getMessage());
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            log.error("Visa integration history throws error: {}", e.getMessage());
            return new SimpleResponse<>(582, e.getMessage());
        }
    }
}
