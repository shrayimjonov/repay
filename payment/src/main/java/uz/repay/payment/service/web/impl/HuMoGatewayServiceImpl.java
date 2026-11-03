package uz.repay.payment.service.web.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.client.HuMoGatewayFeignClient;
import uz.repay.payment.model.dto.humo.request.*;
import uz.repay.payment.model.dto.humo.response.HuMoBaseEmptyResponse;
import uz.repay.payment.model.dto.humo.response.HuMoBasePaymentResponse;
import uz.repay.payment.service.web.HuMoGatewayService;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class HuMoGatewayServiceImpl implements HuMoGatewayService {
    private final HuMoGatewayFeignClient client;
    private final ObjectMapper objectMapper;

    @Override
    public SimpleResponse<HuMoBasePaymentResponse> debit(HuMoDebitRequest request) {
        log.info("Debit request to huMo gate: {}", request);
        try {
            var response = client.debit(request);
            log.info("Debit response from huMo gate: {}", response);
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
    public SimpleResponse<HuMoBasePaymentResponse> debitConfirm(HuMoDebitConfirmRequest request) {
        log.info("Debit Confirm request to huMo gate: {}", request);
        try {
            var response = client.debitConfirm(request);
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
    public SimpleResponse<HuMoBasePaymentResponse> getPaymentByExt(HuMoGetPaymentByExtRequest request) {
        log.info("Get Payment by externalID request to huMo gate: {}", request);
        try {
            var response = client.getPaymentByExt(request);
            log.info("Get Payment by externalID response from huMo gate: {}", response);
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
    public SimpleResponse<HuMoBasePaymentResponse> getPaymentById(HuMoGetPaymentByIdRequest request) {
        log.info("Get Payment by PaymentID request to huMo gate: {}", request);
        try {
            var response = client.getPaymentById(request);
            log.info("Get Payment by PaymentID response from huMo gate: {}", response);
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
    public SimpleResponse<HuMoBaseEmptyResponse> returnByRefNum(HuMoReturnPaymentByExtRequest request) {
        log.info("Return Payment by RefNum request to huMo gate: {}", request);
        try {
            var response = client.returnByRefNum(request);
            log.info("Return Payment by RefNum response from huMo gate: {}", response);
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
    public SimpleResponse<HuMoBaseEmptyResponse> returnById(HuMoReturnPaymentByIdRequest request) {
        log.info("Return Payment by PaymentID request to huMo gate: {}", request);
        try {
            var response = client.returnById(request);
            log.info("Return Payment by PaymentID response from huMo gate: {}", response);
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
    public SimpleResponse<HuMoBaseEmptyResponse> cancelByExt(HuMoCancelPaymentByExtRequest request) {
        log.info("Cancel Payment by RefNum request to huMo gate: {}", request);
        try {
            var response = client.cancelByExt(request);
            log.info("Cancel Payment by RefNum response from huMo gate: {}", response);
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
    public SimpleResponse<HuMoBaseEmptyResponse> cancelById(HuMoCancelPaymentByIdRequest request) {
        log.info("Cancel Payment by PaymentID request to huMo gate: {}", request);
        try {
            var response = client.cancelById(request);
            log.info("Cancel Payment by PaymentID response from huMo gate: {}", response);
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
    public SimpleResponse<Map<String, Object>> executeTransaction(ExecuteTransactionRequest request) {
        log.info("Execute transaction request to huMo gate: {}", toJson(request));
        try {
            var response = client.executeTransaction(request);
            log.info("Execute transaction response from huMo gate: {}", toJson(response));
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Execute transaction retryable error from huMo gate. request: {}, error: {}", toJson(request), r.getMessage(), r);
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("Execute transaction feign error from huMo gate. request: {}, error: {}", toJson(request), f.getMessage(), f);
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            log.error("Execute transaction error from huMo gate. request: {}, error: {}", toJson(request), e.getMessage(), e);
            return new SimpleResponse<>(582, e.getMessage());
        }
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to serialize huMo gateway log object: {}", ex.getMessage());
            return String.valueOf(object);
        }
    }
}
