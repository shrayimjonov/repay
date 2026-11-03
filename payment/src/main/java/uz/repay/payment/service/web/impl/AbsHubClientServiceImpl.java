package uz.repay.payment.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.payment.client.ABSHubFeignClient;
import uz.repay.payment.model.dto.absHub.request.AbsHubTransactionReverseRequest;
import uz.repay.payment.model.dto.absHub.response.AbsHubTransactionReverseResponse;
import uz.repay.payment.service.web.AbsHubClientService;

@Service
@Slf4j
@RequiredArgsConstructor
public class AbsHubClientServiceImpl implements AbsHubClientService {
    private final ABSHubFeignClient client;

    @Override
    public ResultData<AbsHubTransactionReverseResponse> reverse(AbsHubTransactionReverseRequest request) {
        log.info("Reverse transaction request to abs hub: {}", request);
        try {
            var response = client.reverse(request);
            log.info("Reverse transaction from abs hub response: {}", response);
            return response;
        } catch (RetryableException r) {
            log.info("Reverse transaction throws time out error: {}", r.getMessage());
            return new ResultData<>(580, r.getMessage());
        } catch (FeignException f) {
            log.info("Reverse transaction throws feign error: {}", f.getMessage());
            return new ResultData<>(581, f.getMessage());
        } catch (Exception e) {
            log.info("Reverse transaction throws error: {}", e.getMessage());
            return new ResultData<>(582, e.getMessage());
        }
    }
}
