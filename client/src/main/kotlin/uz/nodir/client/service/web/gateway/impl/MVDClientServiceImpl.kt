package uz.nodir.client.service.web.gateway.impl

import feign.FeignException
import feign.RetryableException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uz.nodir.client.client.MVDFeignClient
import uz.nodir.client.model.dto.gateway.request.MVDAddressRequest
import uz.nodir.client.model.dto.gateway.response.MVDAddressResponse
import uz.nodir.client.service.web.gateway.MVDClientService
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.ResultState


@Service
class MVDClientServiceImpl(
    private val client: MVDFeignClient
) : MVDClientService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun clientAddress(personCode: String): SimpleResponse<MVDAddressResponse> {
        val request = MVDAddressRequest(personCode)
        log.info("Sending request to Gateway \"client address\" method from Justice with request: {}", request)
        try {
            val response = client.address(request)
            log.info("Taken response from Gateway from method \"client address\" from Justice with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
            log.error("Taken a TIME OUT error while executing \"client address\" method with message: {}", r.message)
            return SimpleResponse(ResultState.TimeOutError.code, "Gateway TIME OUT: ${r.message}")
        } catch (f: FeignException) {
            log.error("Taken a THIRD PARTY error while executing \"client address\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "Gateway THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
            log.error("Taken an internal out error while executing \"client address\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "Gateway INTERNAL ERROR: ${e.message}")
        }
    }
}