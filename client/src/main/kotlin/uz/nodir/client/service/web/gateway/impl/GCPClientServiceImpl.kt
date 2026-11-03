package uz.nodir.client.service.web.gateway.impl

import uz.nodir.client.client.GCPClientFeignClient
import feign.FeignException
import feign.RetryableException
import uz.nodir.client.model.dto.gcp.request.GatewayClientInfoGCPRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.ResultState
import uz.nodir.client.service.web.gateway.GCPClientService
import uz.repay.common.model.dto.request.gateway.gcp.GatewayClientInfoGCPResponse
import java.time.LocalDate

@Service
class GCPClientServiceImpl(
    val client: GCPClientFeignClient
) : GCPClientService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun clientInfoGCP(
        personCode: String,
        birthday: LocalDate
    ): SimpleResponse<GatewayClientInfoGCPResponse> {
        val request = GatewayClientInfoGCPRequest(personCode, birthday)
        log.info("Sending request to Gateway for take client info from GCP with request: {}", request)
        try {
            val response = client.clientInfoGCP(request)
            log.info("Received response from Gateway for take client info from GCP with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
            log.error(
                "Taken a time out error while executing take client info from GCP method with message: {}",
                r.message
            )
            return SimpleResponse(ResultState.TimeOutError.code, "Gateway TIME OUT: ${r.message}")
        } catch (f: FeignException) {
            log.error(
                "Taken a third party error while executing take client info from GCP method with message: {}",
                f.message
            )
            return SimpleResponse(
                ResultState.ThirdPartyError.code,
                "Gateway THIRD PARTY ERROR: ${f.message}"
            )
        } catch (e: Exception) {
            log.error(
                "Taken an internal out error while executing take client info from GCP method with message: {}",
                e.message
            )
            return SimpleResponse(ResultState.InternalServerError.code, "Gateway INTERNAL ERROR: ${e.message}")
        }
    }
}
