package uz.nodir.client.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import uz.nodir.client.client.configuration.MVDClientConfiguration
import uz.nodir.client.model.dto.gateway.request.MVDAddressRequest
import uz.nodir.client.model.dto.gateway.response.MVDAddressResponse

@FeignClient(name = "mvd", url = "\${external.gateway.mvd.url}", path = "/api/v1/egov", configuration = [MVDClientConfiguration::class])
interface MVDFeignClient {

    @PostMapping("/mvd-address-info-pinfl")
    fun address(request: MVDAddressRequest): MVDAddressResponse
}
