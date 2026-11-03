package uz.nodir.client.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import uz.nodir.client.client.configuration.GCPClientConfiguration
import uz.nodir.client.model.dto.gcp.request.GatewayClientInfoGCPRequest
import uz.repay.common.model.dto.request.gateway.gcp.GatewayClientInfoGCPResponse

@FeignClient(name = "gcp", url = "\${external.gateway.gcp.url}", path = "/api/v1/egov", configuration = [GCPClientConfiguration::class])
interface GCPClientFeignClient {

    @PostMapping("/gsp-citizen")
    fun clientInfoGCP(request: GatewayClientInfoGCPRequest): GatewayClientInfoGCPResponse
}
