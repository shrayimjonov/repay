package uz.nodir.scoring.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.nodir.scoring.client.configuration.MonitoringFeignConfiguration;
import uz.nodir.scoring.model.dto.monitoring.request.MonitoringBaseRequest;
import uz.nodir.scoring.model.dto.monitoring.response.MonitoringBaseResponse;
import uz.nodir.scoring.model.dto.monitoring.response.result.MonitoringResult;

import java.util.List;

@FeignClient(value = "card-monitoring", url = "${external.monitoring.url}", configuration = MonitoringFeignConfiguration.class)
public interface MonitoringFeignClient {

    @PostMapping("/api/v3/history")
    MonitoringBaseResponse<List<MonitoringResult>> monitoringV3(MonitoringBaseRequest request);
}
