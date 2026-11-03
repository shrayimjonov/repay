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
import uz.nodir.client.model.dto.mvd.response.MVDAddressResponseDTO
import uz.nodir.client.service.business.MVDService
import uz.repay.common.model.dto.response.annotations.PersonCode
import uz.repay.common.model.dto.response.core.ResultData


@RestController
@RequestMapping("/api/v1/mvd", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
@Validated
@Tag(name = "mvd-endpoint")
class MVDWebController(
    private val mvdService: MVDService
) {

    @GetMapping("/address")
    @Operation(description = "Get client address from MVD", operationId = "mvd-client-address")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_CLIENT_MVD_ADDRESS)")
    fun address(@RequestParam("person_code") @Valid @PersonCode personCode: String): ResponseEntity<ResultData<MVDAddressResponseDTO>> =
        ResponseEntity.ok(mvdService.address(personCode))



}