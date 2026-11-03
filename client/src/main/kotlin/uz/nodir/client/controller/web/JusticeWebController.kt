package uz.nodir.client.controller.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uz.nodir.client.model.dto.justice.response.JusticeDeathResponseDTO
import uz.nodir.client.model.dto.justice.response.JusticeMarriageResponseDTO
import uz.nodir.client.service.business.JusticeService
import uz.repay.common.model.dto.response.annotations.PersonCode
import uz.repay.common.model.dto.response.core.ResultData


@RestController
@RequestMapping(
    "/api/v1/justice",
    produces = [MediaType.APPLICATION_JSON_VALUE],
    consumes = [MediaType.APPLICATION_JSON_VALUE]
)
@Validated
@Tag(name = "justice-endpoint")
class JusticeWebController(
    private val justiceService: JusticeService
) {

    @GetMapping("/death")
    @Operation(description = "Get client death info", operationId = "justice-death")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_CLIENT_JUSTICE_DEATH)")
    fun death(@RequestParam("person_code") @Valid @PersonCode personCode: String): ResponseEntity<ResultData<List<JusticeDeathResponseDTO>>> =
        ResponseEntity.ok(justiceService.death(personCode))

    @GetMapping("/marriage")
    @Operation(description = "Get client marriage info", operationId = "justice-marriage")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_CLIENT_JUSTICE_MARRIAGE)")
    fun marriage(@RequestParam("person_code") @Valid @PersonCode personCode: String): ResponseEntity<ResultData<List<JusticeMarriageResponseDTO>>> =
        ResponseEntity.ok(justiceService.marriage(personCode))


}