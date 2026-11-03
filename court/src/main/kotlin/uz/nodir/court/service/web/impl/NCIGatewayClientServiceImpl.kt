package uz.nodir.court.service.web.impl

import feign.FeignException
import feign.RetryableException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uz.nodir.court.client.NCIGatewayFeignClient
import uz.nodir.court.model.dto.nciGateway.request.NCIGatewayLoanInfoChambersRequest
import uz.nodir.court.model.dto.nciGateway.response.NCIGatewayLoanInfoChambersResponse
import uz.nodir.court.service.web.NCIGatewayClientService
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.ResultState


@Service
class NCIGatewayClientServiceImpl(
    private val client: NCIGatewayFeignClient
) : NCIGatewayClientService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun loanInfoChambers(loanExtId: String): SimpleResponse<ResultData<NCIGatewayLoanInfoChambersResponse>> {
        val request = NCIGatewayLoanInfoChambersRequest(loanExtId)
          log.info("Sending request to NCIGateway \"loan info chambers\" method with request: $request")
        try {
            val response = client.loanInfoChambers(request)
          log.info("Taken response from method \"loan info chambers\" with response: {}", response)
            return SimpleResponse(response)
        } catch (r: RetryableException) {
          log.error("Taken a TIME OUT error while executing \"loan info chambers\" method with message: {}", r.message)
            return SimpleResponse(ResultState.TimeOutError.code, "NCIGateway TIME OUT: ${r.message}")
        } catch (f: FeignException) {
          log.error("Taken a THIRD PARTY error while executing \"loan info chambers\" method with message: {}", f.message)
            return SimpleResponse(ResultState.ThirdPartyError.code, "NCIGateway THIRD PARTY ERROR: ${f.message}")
        } catch (e: Exception) {
          log.error("Taken an INTERNAL error while executing \"loan info chambers\" method with message: {}", e.message)
            return SimpleResponse(ResultState.InternalServerError.code, "NCIGateway INTERNAL ERROR: ${e.message}")
        }
    }

}