package uz.repay.autodebit.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.autodebit.client.PaymentFeignClient;
import uz.repay.autodebit.model.payment.response.PayResponseDTO;
import uz.repay.autodebit.service.web.PaymentFeignService;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.payment.NCICourseResponse;
import uz.repay.common.model.enums.ResultState;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentFeignServiceImpl implements PaymentFeignService {
    private final PaymentFeignClient client;

    @Override
    public ResultData<PayResponseDTO> pay(PayRequestDTO requestDTO) {
        log.info("Pay request to payment: {}", requestDTO);
        try {
            var response = client.pay(requestDTO);
            log.info("Payment response: {}", response);
            return response;
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }

    @Override
    public ResultData<PayResponseDTO> payV2(PayRequestDTO requestDTO) {
        log.info("Pay request to payment v2: {}", requestDTO);
        try {
            return client.payV2(requestDTO);
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }

    @Override
    public ResultData<PayResponseDTO> payV3(PayRequestDTO requestDTO) {
        log.info("Pay request to payment v3: {}", requestDTO);
        try {
            return client.payV3(requestDTO, 2);
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }

    @Override
    public ResultData<List<NCICourseResponse>> getCourse() {
        log.info("Get course request to payment v2");
        try {
            return client.getCourse();
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }
}
