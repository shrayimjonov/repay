package uz.repay.payment.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.client.BillingFeignClient;
import uz.repay.payment.model.dto.billing.request.CancelRequest;
import uz.repay.payment.model.dto.billing.request.CheckRequest;
import uz.repay.payment.model.dto.billing.request.PayRequest;
import uz.repay.payment.model.dto.billing.response.BillingBaseResponse;
import uz.repay.payment.model.dto.billing.response.PaymentInfoResponse;
import uz.repay.payment.model.dto.billing.response.result.DataResult;
import uz.repay.payment.model.dto.billing.response.result.SRData;
import uz.repay.payment.model.property.BillingProperty;
import uz.repay.payment.service.web.BillingService;


@Service
@RequiredArgsConstructor
@Slf4j
public class BillingServiceImpl implements BillingService {
    private final BillingFeignClient client;
    private final BillingProperty property;

    @Override
    public SimpleResponse<BillingBaseResponse<?>> check(CheckRequest request) {
        log.info("Check request to billing: {}", request);
        try {
            var response = client.check(request);
            log.info("Check response: {}", response);

            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(582, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<BillingBaseResponse<DataResult<SRData>>> pay(PayRequest request) {
        log.info("Pay request to billing: {}", request);
        try {
            var response = client.pay(request);
            log.info("Pay response: {}", response);

            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(582, e.getMessage());
        }
    }


    @Override
    public SimpleResponse<BillingBaseResponse<?>> cancel(CancelRequest request) {
        log.info("Cancel request to billing: {}", request);
        try {
            var response = client.cancel(request);
            log.info("Cancel response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(582, e.getMessage());
        }
    }


    @Override
    public SimpleResponse<PaymentInfoResponse> getPayment(String requestTrId) {
        log.info("Get Payment request to billing\n\n" +
                property.getUrl() + "/services/bmms/api/transactions/getByRequestTrId/%s".formatted(requestTrId));
        try {
            var response = client.getPayInfoByTranId(requestTrId);
            log.info("Get Payment response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(582, e.getMessage());
        }
    }


}
