package uz.nodir.court.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import uz.nodir.court.client.configuration.ChamberFeignClientConfiguration
import uz.nodir.court.model.dto.chambers.request.ChamberClaimRequest
import uz.nodir.court.model.dto.chambers.response.*


@FeignClient(
    name = "chambers",
    url = "\${external.chambers.url}",
    path = "/api/ExternalClaimApplication",
    configuration = [ChamberFeignClientConfiguration::class]
)
interface ChamberFeignClient {

    @PostMapping("/PostClaimApplicationDavrBank")
    fun postClaimApplication(request: ChamberClaimRequest): ChamberClaimResponse

    @GetMapping("/GetClaimApplicationType")
    fun getClaimApplicationTypes(): List<ChamberClaimDefaultTypeResponse>

    @GetMapping("/GetClaimTheme")
    fun getClaimThemes(): List<ChamberClaimDefaultTypeResponse>

    @GetMapping("/ClaimResponsibleType")
    fun getClaimResponsibleTypes(): List<ChamberClaimDefaultTypeResponse>

    @GetMapping("/GetCurrency")
    fun getCurrencies(): List<ChamberClaimCurrencyTypeResponse>

    @GetMapping("/GetOrganization")
    fun getOrganizations(): List<ChamberClaimDefaultTypeResponse>

    @GetMapping("/GetLanguage")
    fun getLanguages(): List<ChamberClaimLanguageTypeResponse>

    @GetMapping("/GetForIntegration/{id}")
    fun getClaimById(@PathVariable("id") id: String): ChamberGetClaimResponse?

}