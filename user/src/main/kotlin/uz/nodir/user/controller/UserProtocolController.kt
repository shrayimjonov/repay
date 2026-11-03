package uz.nodir.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uz.nodir.user.model.dto.userProtocol.request.UserProtocolFilterRequestDTO
import uz.nodir.user.service.business.persistence.UserProtocolService


@RestController
@RequestMapping("/api/v1/protocol", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "user-protocol-endpoint")
class UserProtocolController(
    private val userProtocolService: UserProtocolService
) {

    @PostMapping("/byUser")
    @Operation(
        summary = "User protocols",
        description = "Get user protocols",
        operationId = "user-user-protocol"
    )
    fun getUserProtocol(@Valid @RequestBody requestDTO: UserProtocolFilterRequestDTO) =
        ResponseEntity.ok(userProtocolService.getUserProtocol(requestDTO))
}