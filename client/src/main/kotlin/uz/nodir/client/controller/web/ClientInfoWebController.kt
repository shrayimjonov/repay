package uz.nodir.client.controller.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uz.nodir.client.service.business.GCPService
import uz.repay.common.model.dto.request.client.ClientInfoRequestDTO
import uz.repay.common.model.dto.request.gateway.gcp.GatewayClientInfoGCPResponse
import uz.repay.common.model.dto.response.core.ResultData


@RestController
@RequestMapping("/api/v1/gcp", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "gcp-endpoint")
class ClientInfoWebController(
    private val gcpClientService: GCPService
) {

    @PostMapping("/info")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REPAY_CLIENT_GCP_INFO)")
    @Operation(description = "Take client info from GCP", operationId = "client-gcp-info")
    fun clientInfo(@RequestBody @Valid requestDTO: ClientInfoRequestDTO): ResponseEntity<ResultData<GatewayClientInfoGCPResponse>> =
        ResponseEntity.ok(gcpClientService.clientInfo(requestDTO))
}