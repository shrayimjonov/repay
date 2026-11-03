package uz.repay.payment.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.client.UzCardIntegrationFeignClient;
import uz.repay.payment.model.dto.uzcard.request.TransPayDto;
import uz.repay.payment.model.dto.uzcard.request.UzCardBaseRequest;
import uz.repay.payment.model.dto.uzcard.request.params.TransPayParam;
import uz.repay.payment.model.dto.uzcard.request.params.TransPayPurposeParam;
import uz.repay.payment.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.payment.model.dto.uzcard.response.result.TransPayPurposeResult;
import uz.repay.payment.model.dto.uzcard.response.result.UzCardBasePaymentResult;
import uz.repay.payment.service.web.UzCardIntegrationService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UzCardIntegrationServiceImpl implements UzCardIntegrationService {
    private final UzCardIntegrationFeignClient client;


    @Override
    public SimpleResponse<UzCardBaseResponse<TransPayPurposeResult>> pay(UzCardBaseRequest<TransPayPurposeParam> request) {
        log.info("Pay request to uzcard_integration: {}", request);
        try {
            var response = client.transPayPurpose(request);
            log.info("Pay response  from uzcard_integration: {}", response);
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
    public SimpleResponse<UzCardBaseResponse<TransPayPurposeResult>> pay2(TransPayDto request) {
        log.info("Pay request to uzcard_integration: {}", request);
        try {
            var response = client.transPay(request);
            log.info("Pay response from uzcard_integration: {}", response);
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
    public SimpleResponse<UzCardBaseResponse<List<UzCardBasePaymentResult>>> reverse(String refNum) {
        log.info("Reverse request to uzcard_integration: {}", refNum);
        try {
            var response = client.reverse(refNum);
            log.info("Reverse response from uzcard_integration: {}", response);
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
    public SimpleResponse<UzCardBaseResponse<List<UzCardBasePaymentResult>>> getPaymentByRefNum(String refNum) {
        log.info("Get payment by Ref Num request to uzcard_integration: {}", refNum);
        try {
            var response = client.getPaymentByRefNum(refNum);
            log.info("Get payment by Ref Num response from uzcard_integration: {}", response);
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
    public SimpleResponse<UzCardBaseResponse<List<UzCardBasePaymentResult>>> getPaymentById(String paymentId) {
        log.info("Get payment by PaymentID request to uzcard_integration: {}", paymentId);
        try {
            var response = client.getPaymentById(paymentId);
            log.info("Get payment by PaymentID response from uzcard_integration: {}", response);
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
