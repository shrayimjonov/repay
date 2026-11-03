package uz.nodir.user.model.dto.userProtocol.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import uz.repay.common.model.dto.request.core.params.DateRange

data class UserProtocolRequestDTO(

    @JsonProperty("user_name")
    @field:NotBlank(message = "User name cannot be null")
    val userName: String? = null,

    @JsonProperty("first_name")
    val firstName: String? = null,

    @JsonProperty("last_name")
    val lastName: String? = null,

    @JsonProperty("ip_address")
    val ipAddress: String? = null,

    @JsonProperty("date_range")
    val dateRange: DateRange? = null,
) {
}