package uz.nodir.scoring.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.scoring.client.HuMoGatewayFeignClient;
import uz.nodir.scoring.model.dto.huMoGateway.request.HuMoGateCardLimitRequest;
import uz.nodir.scoring.model.dto.huMoGateway.response.HuMoGateCardLimitResponse;
import uz.nodir.scoring.model.dto.monitoring.request.MonitoringBaseRequest;
import uz.nodir.scoring.service.web.HuMoGateClientService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;


@Service
@Slf4j
@RequiredArgsConstructor
public class HuMoGateClientServiceImpl implements HuMoGateClientService {
    private final HuMoGatewayFeignClient client;

    @Override
    public SimpleResponse<HuMoGateCardLimitResponse> getCardLimits(String pan) {
        var request = new HuMoGateCardLimitRequest(
                pan
        );
        log.info("Sending request to huMo gateway client to method \"get card limits\" with request: {}", request);

        try {
            var response = client.getCardLimits(request);
            log.info("Taken response from huMo gateway client from \"get card limits\" method with response: {}", response.toString());
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Taken a TIME OUT error from huMo gateway client from \"get card limits\" method with message: {}", r.getMessage());
            return new SimpleResponse<>(r.getMessage(), true);
        } catch (FeignException f) {
            log.error("Taken a THIRD PARTY error from huMo gateway client from \"get card limits\" method with message: {}", f.getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), f.getMessage());
        } catch (Exception e) {
            log.error("Taken an INTERNAL error from huMo gateway client from \"get card limits\" method with message: {}", e.getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }
}
