package uz.repay.payment.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.client.MasterGatewayFeignClient;
import uz.repay.payment.model.dto.master.request.*;
import uz.repay.payment.model.dto.master.response.MasterPaymentResponse;
import uz.repay.payment.service.web.MasterGatewayClientService;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterGatewayClientServiceImpl implements MasterGatewayClientService {
    private final MasterGatewayFeignClient client;

    @Override
    public SimpleResponse<ResultData<MasterPaymentResponse>> debit(MasterDebitRequest request) {
        var gateRequest = new MasterBaseRequest<>(
                System.currentTimeMillis(),
                request
        );
        log.info("Send to MASTER gateway debit request: {}", gateRequest);

        try {
            var response = client.debit(
                    gateRequest
            );
            log.info("Received MASTER gateway debit response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("MASTER gateway debit method throws error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("MASTER gateway debit method throws error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("MASTER gateway debit method throws error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }


    @Override
    public SimpleResponse<ResultData<MasterPaymentResponse>> syncByExtId(MasterSyncByExtIdRequest request) {
        var gateRequest = new MasterBaseRequest<>(
                System.currentTimeMillis(),
                request
        );
        log.info("Send to MASTER gateway sync by extId request: {}", gateRequest);

        try {
            var response = client.syncByExtId(
                    gateRequest
            );
            log.info("Received MASTER gateway sync by extId response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("MASTER gateway sync by extId method throws error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("MASTER gateway sync by extId method throws error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("MASTER gateway sync by extId method throws error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<MasterPaymentResponse>> syncByRRN(MasterSyncByRRNRequest request) {
        var gateRequest = new MasterBaseRequest<>(
                System.currentTimeMillis(),
                request
        );
        log.info("Send to MASTER gateway sync by rrn request: {}", gateRequest);

        try {
            var response = client.syncByRRN(
                    gateRequest
            );
            log.info("Received MASTER gateway sync by rrn response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("MASTER gateway sync by rrn method throws error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("MASTER gateway sync by rrn method throws error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("MASTER gateway sync by rrn method throws error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }


    @Override
    public SimpleResponse<ResultData<MasterPaymentResponse>> reverseByExtId(MasterReverseByExtIdRequest request) {
        var gateRequest = new MasterBaseRequest<>(
                System.currentTimeMillis(),
                request
        );
        log.info("Send to MASTER gateway reverse by extId request: {}", gateRequest);

        try {
            var response = client.reverseByExtId(
                    gateRequest
            );
            log.info("Received MASTER gateway reverse by extId response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("MASTER gateway reverse by extId method throws error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("MASTER gateway reverse by extId method throws error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("MASTER gateway reverse by extId method throws error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }


}
