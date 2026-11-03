package uz.repay.card.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.client.MasterGatewayFeignClient;
import uz.repay.card.model.dto.master.request.*;
import uz.repay.card.model.dto.master.response.*;
import uz.repay.card.service.web.MasterClientService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterClientServiceImpl implements MasterClientService {
    private final MasterGatewayFeignClient client;

    @Override
    public SimpleResponse<ResultData<MasterOTPVerifyResponse>> otpVerify(MasterOTPVerifyRequest request) {
        var gateRequest = new MasterBaseRequest<>(
                System.currentTimeMillis(),
                request
        );
        log.info("Send to MASTER gateway OTP verify request: {}", gateRequest);

        try {
            var response = client.otpVerify(
                    gateRequest
            );
            log.info("Received MASTER gateway OTP verify response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("MASTER gateway OTP verify method throws error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("MASTER gateway OTP verify method throws error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("MASTER gateway OTP verify method throws error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<MasterOTPConfirmResponse>> otpConfirm(MasterOTPConfirmRequest request) {
        var gateRequest = new MasterBaseRequest<>(
                System.currentTimeMillis(),
                request
        );
        log.info("Send to MASTER gateway OTP confirm request: {}", gateRequest);

        try {
            var response = client.otpConfirm(
                    gateRequest
            );
            log.info("Received MASTER gateway OTP confirm response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("MASTER gateway OTP confirm method throws error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("MASTER gateway OTP confirm method throws error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("MASTER gateway OTP confirm method throws error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<MasterP2PInfoResponse>> p2pInfo(MasterP2PInfoRequest request) {
        var gateRequest = new MasterBaseRequest<>(
                System.currentTimeMillis(),
                request
        );
        log.info("Send to MASTER gateway p2pInfo request: {}", gateRequest);

        try {
            var response = client.p2pInfo(
                    gateRequest
            );
            log.info("Received MASTER gateway p2pInfo response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("MASTER gateway p2pInfo method throws error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("MASTER gateway p2pInfo method throws error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("MASTER gateway p2pInfo method throws error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<MasterBalanceResponse>> balance(MasterBalanceRequest request) {
        var gateRequest = new MasterBaseRequest<>(
                System.currentTimeMillis(),
                request
        );
        log.info("Send to MASTER gateway balance request: {}", gateRequest);

        try {
            var response = client.balance(
                    gateRequest
            );
            log.info("Received MASTER gateway balance response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("MASTER gateway balance method throws error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("MASTER gateway balance method throws error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("MASTER gateway balance method throws error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<List<MasterCustomerCardsResponse>>> customerCards(MasterCustomerCardsRequest request) {
        var gateRequest = new MasterBaseRequest<>(
                System.currentTimeMillis(),
                request
        );
        log.info("Send to MASTER gateway get customer cards by customerId request: {}", gateRequest);

        try {
            var response = client.customerCards(
                    gateRequest
            );
            log.info("Received MASTER gateway get customer cards by customerId method response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("MASTER gateway get customer cards by customerId method throws error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("MASTER gateway get customer cards by customerId method throws error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("MASTER gateway get customer cards by customerId method throws error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<ArrayList<MasterCustomerCardsResponse>>> customerCardsByPersonCode(MasterCustomerCardsByPINFLRequest request) {
        var gateRequest = new MasterBaseRequest<>(
                System.currentTimeMillis(),
                request
        );
        log.info("Send to MASTER gateway get customer cards by person code request: {}", gateRequest);

        try {
            var response = client.customerCardsByPersonCode(
                    gateRequest
            );
            log.info("Received MASTER gateway get customer cards by person code method response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("MASTER gateway get customer cards by person code method throws error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("MASTER gateway get customer cards by person code method throws error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("MASTER gateway get customer cards by person code method throws error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }
}
