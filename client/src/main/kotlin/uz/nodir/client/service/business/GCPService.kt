package uz.nodir.client.service.business

import jakarta.annotation.Nonnull
import uz.repay.common.model.dto.request.client.ClientInfoRequestDTO
import uz.repay.common.model.dto.request.gateway.gcp.GatewayClientInfoGCPResponse
import uz.repay.common.model.dto.response.core.ResultData

interface GCPService {
    fun clientInfo(@Nonnull requestDTO: ClientInfoRequestDTO): ResultData<GatewayClientInfoGCPResponse>
}
