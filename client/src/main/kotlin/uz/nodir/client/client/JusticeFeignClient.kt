package uz.nodir.client.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import uz.nodir.client.client.configuration.JusticeClientFeignConfiguration
import uz.nodir.client.model.dto.gateway.request.JusticeDeathRequest
import uz.nodir.client.model.dto.gateway.request.JusticeMarriageRequest
import uz.nodir.client.model.dto.gateway.response.JusticeBaseResponse
import uz.nodir.client.model.dto.gateway.response.result.JusticeDeathResult
import uz.nodir.client.model.dto.gateway.response.result.JusticeMarriageResult


@FeignClient(
    name = "justice",
    url = "\${external.gateway.justice.url}",
    path = "/api/v1/egov",
    configuration = [JusticeClientFeignConfiguration::class]
)
interface JusticeFeignClient {

    @PostMapping("/justice-marriage")
    fun marriage(request: JusticeMarriageRequest): JusticeBaseResponse<List<JusticeMarriageResult>>


    @PostMapping("/justice-death")
    fun death(request: JusticeDeathRequest): JusticeBaseResponse<List<JusticeDeathResult>>

}