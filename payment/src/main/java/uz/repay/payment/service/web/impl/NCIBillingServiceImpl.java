package uz.repay.payment.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.payment.NCICourseResponse;
import uz.repay.payment.client.NCIBillingFeignClient;
import uz.repay.payment.model.dto.nciBilling.request.NCIForexTranRequest;
import uz.repay.payment.model.dto.nciBilling.request.NCIPayRequest;
import uz.repay.payment.model.dto.nciBilling.request.NCIReverseDTO;
import uz.repay.payment.model.dto.nciBilling.request.NCISimpleTranRequest;
import uz.repay.payment.model.dto.nciBilling.response.*;
import uz.repay.payment.service.web.NCIBillingService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NCIBillingServiceImpl implements NCIBillingService {
    private final NCIBillingFeignClient client;

    @Override
    public SimpleResponse<NCICheckResponse<NCIBaseResponse>> checkPaymentFromABS(String paymentId) {
        log.info("Check transaction from nci by refNum: {}", paymentId);
        try {
            var response = client.check(paymentId);
            log.info("Check abs transaction response: {}", response);
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
    @Deprecated
    public SimpleResponse<NCIBaseResponse> reverseABSTransaction(NCIReverseDTO requestDTO) {
        log.info("Reverse transaction: {} in: {}", requestDTO.getGeneralId(), requestDTO.getBranch());
        try {
            var response = client.reverse(requestDTO);
            log.info("Reverse transaction response: {}", response);

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
    public SimpleResponse<ResultData<ProblemResponse>> pay(NCIPayRequest request) {
        log.info("Pay transaction request: {}", request);
        try {
            var response = client.pay(request);
            log.info("Pay transaction response: {}", response);
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
    public SimpleResponse<NCIBaseResponse> doForex(NCIForexTranRequest request) {
        log.info("ABS Forex transaction request: {}", request);
        try {
            var response = client.doForex(request);
            log.info("ABS Forex transaction response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("ABS Forex transaction has TIME OUT error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("ABS Forex transaction has FEIGN error: {}", f.getMessage());
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            log.error("ABS Forex transaction has error: {}", e.getMessage());
            return new SimpleResponse<>(582, e.getMessage());
        }
    }


    @Override
    public SimpleResponse<NCIBaseResponse> doSimpleTransaction(NCISimpleTranRequest request) {
        log.info("ABS simple transaction request: {}", request);
        try {
            var response = client.doSimpleTransaction(request);
            log.info("ABS simple transaction response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("ABS simple transaction has TIME OUT error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("ABS simple transaction has FEIGN error: {}", f.getMessage());
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            log.error("ABS simple transaction has error: {}", e.getMessage());
            return new SimpleResponse<>(582, e.getMessage());
        }
    }


    @Override
    public SimpleResponse<NCIDayResponse> getBankDay() {
        log.info("ABS get bank day request");
        try {
            var response = client.getBankDay();
            log.info("ABS get bank day response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("ABS get bank day method has TIME OUT error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("ABS get bank day method has FEIGN error: {}", f.getMessage());
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            log.error("ABS get bank day method has error: {}", e.getMessage());
            return new SimpleResponse<>(582, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<List<NCICourseResponse>>> getCourse() {
        log.info("ABS get course request");
        try {
            var response = client.getCourse();
            log.info("ABS get course response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("ABS get bank day method has TIME OUT error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("ABS get bank day method has FEIGN error: {}", f.getMessage());
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            log.error("ABS get bank day method has error: {}", e.getMessage());
            return new SimpleResponse<>(582, e.getMessage());
        }
    }


    @Override
    public ResultData<NCITransactionResponse> getTransactionById(String absTranId) {
        log.info("Get transaction from abs request by id: {}", absTranId);
        try {
            var response = client.getTransactionById(absTranId);
            log.info("Get transaction from abs response: {}", response);
            return response;
        } catch (RetryableException r) {
            log.error("Get transaction from abs method has TIME OUT error: {}", r.getMessage());
            return new ResultData<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("Get transaction from abs method has FEIGN error: {}", f.getMessage());
            return new ResultData<>(581, f.getMessage());
        } catch (Exception e) {
            log.error("AGet transaction from abs method has error: {}", e.getMessage());
            return new ResultData<>(582, e.getMessage());
        }
    }

}
