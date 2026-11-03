package uz.nodir.scoring.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.scoring.client.MonitoringFeignClient;
import uz.nodir.scoring.model.dto.monitoring.request.MonitoringBaseRequest;
import uz.nodir.scoring.model.dto.monitoring.response.MonitoringBaseResponse;
import uz.nodir.scoring.model.dto.monitoring.response.result.MonitoringResult;
import uz.nodir.scoring.service.web.MonitoringClientService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MonitoringClientServiceImpl implements MonitoringClientService {
    private final MonitoringFeignClient client;

    @Override
    public SimpleResponse<MonitoringBaseResponse<List<MonitoringResult>>> monitoringV3(MonitoringBaseRequest request) {
        log.info("Sending to card monitoring service, request: {}", request);

        try {
            var response = client.monitoringV3(request);

            log.info("Received from card monitoring service, response: {}", response.toString());
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Taken a time out exception from card monitoring v3 method with error: {}", r.getMessage());
            return new SimpleResponse<>(r.getMessage(), true);
        } catch (FeignException f) {
            log.error("Taken a feign exception from card monitoring v3 method with error: {}", f.getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), f.getMessage());
        } catch (Exception e) {
            log.error("Taken an internal server exception from card monitoring v3 method with error: {}", e.getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }
}
