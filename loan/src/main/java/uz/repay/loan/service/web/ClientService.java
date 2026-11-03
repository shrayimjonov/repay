package uz.repay.loan.service.web;

import uz.repay.common.model.dto.request.gateway.gcp.GatewayClientInfoGCPResponse;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.time.LocalDate;

public interface ClientService {
    SimpleResponse<ResultData<GatewayClientInfoGCPResponse>> getClientInfo(String personCode, LocalDate birthday);
}
