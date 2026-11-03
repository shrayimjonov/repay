package uz.nodir.client.service.business.impl

import jakarta.annotation.Nonnull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uz.nodir.client.service.business.GCPService
import uz.nodir.client.service.web.gateway.GCPClientService
import uz.repay.common.model.dto.request.client.ClientInfoRequestDTO
import uz.repay.common.model.dto.request.gateway.gcp.GatewayClientInfoGCPResponse
import uz.repay.common.model.dto.response.core.ResultData


@Service
class GCPServiceImpl(
    private val gcpGCPClientService: GCPClientService
) : GCPService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun clientInfo(@Nonnull requestDTO: ClientInfoRequestDTO): ResultData<GatewayClientInfoGCPResponse> {
        log.info("Received command to get client info with request dto: $requestDTO")

        val result = gcpGCPClientService.clientInfoGCP(requestDTO.personCode, requestDTO.birthday)

        if (!result.status) {
            log.error("Finished command. Cause: ${result.error}")
            return ResultData(result.error, false)
        }

        val response = ResultData.ok(
            result.data
        )

        log.info("Finished command to get client info with response dto: $response")
        return response
    }
}