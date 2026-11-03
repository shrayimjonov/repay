package uz.nodir.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uz.nodir.user.model.dto.trustedUser.request.TrustedUserFilterRequestDTO
import uz.nodir.user.service.business.persistence.TrustedUsersService


@RestController
@RequestMapping("/api/v1/trusted_users", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "trusted-users-endpoint")
class TrustedUsersController(
    private val trustedUsersService: TrustedUsersService
) {

    @PostMapping("/byUser")
    @Operation(
        summary = "Trusted users",
        description = "Get trusted users list",
        operationId = "user-trusted-users-find-by-user",
    )
    fun getTrustedUsers(@Valid @RequestBody requestDTO: TrustedUserFilterRequestDTO) =
        ResponseEntity.ok(trustedUsersService.getTrustedUsers(requestDTO))

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete TU",
        description = "Delete trusted user by id",
        operationId = "user-trusted-users-delete-by-id"
    )
    fun deleteTUbyId(@PathVariable id: Long) =
        ResponseEntity.ok(trustedUsersService.deleteTrustedUserById(id))
}