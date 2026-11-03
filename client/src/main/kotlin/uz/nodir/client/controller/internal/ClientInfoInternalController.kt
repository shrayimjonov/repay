package uz.nodir.client.controller.internal

import io.swagger.v3.oas.annotations.Hidden
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uz.nodir.client.service.business.GCPService
import uz.repay.common.model.dto.request.client.ClientInfoRequestDTO
import uz.repay.common.model.dto.request.gateway.gcp.GatewayClientInfoGCPResponse
import uz.repay.common.model.dto.response.core.ResultData


@RestController
@RequestMapping("/api/v1/internal/client", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
@Hidden
class ClientInfoInternalController(
    private val gcpClientService: GCPService
) {

    @PostMapping("/info")
    fun clientInfo(@RequestBody @Valid requestDTO: ClientInfoRequestDTO): ResponseEntity<ResultData<GatewayClientInfoGCPResponse>> =
        ResponseEntity.ok(gcpClientService.clientInfo(requestDTO))
}