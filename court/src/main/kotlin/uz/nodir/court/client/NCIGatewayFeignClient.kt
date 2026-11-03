package uz.nodir.court.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import uz.nodir.court.client.configuration.NCIGatewayFeignClientConfiguration
import uz.nodir.court.model.dto.nciGateway.request.NCIGatewayLoanInfoChambersRequest
import uz.nodir.court.model.dto.nciGateway.response.NCIGatewayLoanInfoChambersResponse
import uz.repay.common.model.dto.response.core.ResultData


@FeignClient(name = "nci-gateway", url = "\${external.nci-gateway.url}", configuration = [NCIGatewayFeignClientConfiguration::class])
interface NCIGatewayFeignClient {

    @PostMapping("/loan/info/chambers")
    fun loanInfoChambers(request: NCIGatewayLoanInfoChambersRequest): ResultData<NCIGatewayLoanInfoChambersResponse>

}