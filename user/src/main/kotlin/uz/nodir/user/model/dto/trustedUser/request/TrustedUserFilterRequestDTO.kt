package uz.nodir.user.model.dto.trustedUser.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import uz.repay.common.model.dto.request.core.params.PageParams


data class TrustedUserFilterRequestDTO(
    @JsonProperty("params")
    @Schema(name = "params", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Filter trusted user by params")
    val params: TrustedUserRequestDTO
): PageParams()