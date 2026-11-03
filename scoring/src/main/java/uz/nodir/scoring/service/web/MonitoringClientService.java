package uz.nodir.scoring.service.web;

import uz.nodir.scoring.model.dto.monitoring.request.MonitoringBaseRequest;
import uz.nodir.scoring.model.dto.monitoring.response.MonitoringBaseResponse;
import uz.nodir.scoring.model.dto.monitoring.response.result.MonitoringResult;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.List;

public interface MonitoringClientService {
    SimpleResponse<MonitoringBaseResponse<List<MonitoringResult>>> monitoringV3(MonitoringBaseRequest request);
}
