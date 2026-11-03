package uz.nodir.client.service.web.gateway.impl

import feign.FeignException
import feign.RetryableException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uz.nodir.client.client.JusticeFeignClient
import uz.nodir.client.model.dto.gateway.request.JusticeDeathRequest
import uz.nodir.client.model.dto.gateway.request.JusticeMarriageRequest
import uz.nodir.client.model.dto.gateway.response.JusticeBaseResponse
import uz.nodir.client.model.dto.gateway.response.result.JusticeDeathResult
import uz.nodir.client.model.dto.gateway.response.result.JusticeMarriageResult
import uz.nodir.client.service.web.gateway.JusticeClientService
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.ResultState


@Service
class JusticeClientServiceImpl(
    private val client: JusticeFeignClient
): JusticeClientService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }


   override fun marriage(personCode: String): SimpleResponse<JusticeBaseResponse<List<JusticeMarriageResult>>> {
        val request = JusticeMarriageRequest(personCode)
        log.info("Sending request to Gateway \"take marriage info\" method from Justice with request: {}", request)
        try {
            val response = client.marriage(request)
            log.info("Taken response from Gateway from method \"take marriage info\" from Justice with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
            log.error("Taken a TIME OUT error while executing \"take marriage info\" method with message: {}", r.message)
            return SimpleResponse(ResultState.TimeOutError.code, "Gateway TIME OUT: ${r.message}")
        } catch (f: FeignException) {
            log.error("Taken a THIRD PARTY error while executing \"take marriage info\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "Gateway THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
            log.error("Taken an internal out error while executing \"take marriage info\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "Gateway INTERNAL ERROR: ${e.message}")
        }
    }

    override fun death(personCode: String): SimpleResponse<JusticeBaseResponse<List<JusticeDeathResult>>> {
        val request = JusticeDeathRequest(personCode)
        log.info("Sending request to Gateway \"take death info\" method from Justice with request: {}", request)
        try {
            val response = client.death(request)
            log.info("Taken response from Gateway from method \"take death info\" from Justice with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
            log.error("Taken a TIME OUT error while executing \"take death info\" method with message: {}", r.message)
            return SimpleResponse(ResultState.TimeOutError.code, "Gateway TIME OUT: ${r.message}")
        } catch (f: FeignException) {
            log.error("Taken a THIRD PARTY error while executing \"take death info\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "Gateway THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
            log.error("Taken an internal out error while executing \"take death info\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "Gateway INTERNAL ERROR: ${e.message}")
        }
    }


}