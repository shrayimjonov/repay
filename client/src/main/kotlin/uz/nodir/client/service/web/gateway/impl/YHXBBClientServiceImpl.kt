package uz.nodir.client.service.web.gateway.impl

import feign.FeignException
import feign.RetryableException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uz.nodir.client.client.YHXBBFeignClient
import uz.nodir.client.model.dto.gateway.request.MVDAddressRequest
import uz.nodir.client.model.dto.gateway.request.YHXBBPersonCarRequest
import uz.nodir.client.model.dto.gateway.response.YHXBBPersonCarResponse
import uz.nodir.client.service.web.gateway.YHXBBClientService
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.ResultState


@Service
class YHXBBClientServiceImpl(
    private val client: YHXBBFeignClient
) : YHXBBClientService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun personCars(personCode: String): SimpleResponse<YHXBBPersonCarResponse> {
        val request = YHXBBPersonCarRequest(personCode)
       log.info("Sending request to Gateway \"person cars\" method from YHXBB with request: {}", request)
        try {
            val response = client.personCars(request)
           log.info("Taken response from Gateway from method \"person cars\" from YHXBB with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
           log.error("Taken a TIME OUT error while executing \"person cars\" method with message: {}", r.message)
            return SimpleResponse(ResultState.TimeOutError.code, "Gateway TIME OUT: ${r.message}")
        } catch (f: FeignException) {
          log.error("Taken a THIRD PARTY error while executing \"person cars\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "Gateway THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
           log.error("Taken an internal out error while executing \"person cars\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "Gateway INTERNAL ERROR: ${e.message}")
        }
    }
}