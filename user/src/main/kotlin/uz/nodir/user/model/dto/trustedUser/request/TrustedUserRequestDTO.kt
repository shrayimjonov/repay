package uz.nodir.user.model.dto.trustedUser.request

import com.fasterxml.jackson.annotation.JsonProperty
import uz.repay.common.model.dto.request.core.params.DateRange
import java.time.LocalDateTime


data class TrustedUserRequestDTO(

    @JsonProperty("user_name")
    val userName: String? = null,

    @JsonProperty("ip_address")
    val ipAddress: String? = null,

    @JsonProperty("date_range")
    val dateRange: DateRange? = null,
) {
}