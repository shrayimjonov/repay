package uz.nodir.user.model.dto.userProtocol.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import uz.repay.common.model.dto.request.core.params.PageParams


data class UserProtocolFilterRequestDTO(
    @JsonProperty("params")
    @Schema(name = "params", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Filter user protocol by params")
    @field:Valid
    @field:NotNull(message = "User protocol params cannot be null")
    val params: UserProtocolRequestDTO
): PageParams()