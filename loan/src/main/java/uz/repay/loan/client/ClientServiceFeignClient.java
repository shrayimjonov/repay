package uz.repay.loan.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.common.model.dto.request.client.ClientInfoRequestDTO;
import uz.repay.common.model.dto.request.gateway.gcp.GatewayClientInfoGCPResponse;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.client.configuration.ClientServiceFeignConfiguration;


@FeignClient(name = "client", path = "/client", configuration = ClientServiceFeignConfiguration.class)
public interface ClientServiceFeignClient {

    @PostMapping("/api/v1/internal/client/info")
    ResultData<GatewayClientInfoGCPResponse> getClientInfo(@Valid ClientInfoRequestDTO request);


}
