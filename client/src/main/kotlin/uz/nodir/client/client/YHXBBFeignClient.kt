package uz.nodir.client.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import uz.nodir.client.client.configuration.YHXBBClientConfiguration
import uz.nodir.client.model.dto.gateway.request.YHXBBPersonCarRequest
import uz.nodir.client.model.dto.gateway.response.YHXBBPersonCarResponse


@FeignClient(name = "yhxbb", url = "\${external.gateway.yhxbb.url}", path = "/api/v1/egov", configuration = [YHXBBClientConfiguration::class])
interface YHXBBFeignClient {

    @PostMapping("/yhxbb-vehicleinfo-by-pinfl")
    fun personCars(request: YHXBBPersonCarRequest): YHXBBPersonCarResponse
}