package uz.nodir.client.controller.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uz.nodir.client.model.dto.yhxbb.response.YHXBBPersonCarResponseDTO
import uz.nodir.client.service.business.YHXBBService
import uz.repay.common.model.dto.response.annotations.PersonCode
import uz.repay.common.model.dto.response.core.ResultData


@RestController
@RequestMapping("/api/v1/yhxbb", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "yhxbb-endpoint", description = "Yol Harakati Xavfsizligi Bosh Boshqarmasi")
class YHXBBWebController(
    private val service: YHXBBService
) {

    @GetMapping("/person-cars")
    @Operation(description = "Get person cars from YHXBB", operationId = "yhxbb-client-address")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_CLIENT_YHXBB_PERSON_CARS)")
    fun personCars(@RequestParam("person_code") @Valid @PersonCode personCode: String): ResponseEntity<ResultData<YHXBBPersonCarResponseDTO>> =
        ResponseEntity.ok(service.personCars(personCode))



}