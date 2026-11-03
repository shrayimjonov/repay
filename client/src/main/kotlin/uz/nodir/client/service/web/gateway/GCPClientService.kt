package uz.nodir.client.service.web.gateway

import uz.repay.common.model.dto.request.gateway.gcp.GatewayClientInfoGCPResponse
import uz.repay.common.model.dto.response.core.SimpleResponse
import java.time.LocalDate

interface GCPClientService {
    fun clientInfoGCP(personCode: String, birthday: LocalDate): SimpleResponse<GatewayClientInfoGCPResponse>
}
