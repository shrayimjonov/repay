package uz.nodir.user.model.dto.trustedUser.response

import com.fasterxml.jackson.annotation.JsonProperty
import uz.nodir.user.model.entity.TrustedUser
import java.time.LocalDateTime


data class TrustedUserResponseDTO(

    @JsonProperty("id")
    val id: Long? = null,

    @JsonProperty("user_name")
    val userName: String? = null,

    @JsonProperty("ip_address")
    val ip: String? = null,

    @JsonProperty("created_time")
    val createdTime: LocalDateTime? = null,
) {

    companion object {

        fun from(trustedUser: TrustedUser): TrustedUserResponseDTO {
            return TrustedUserResponseDTO(
                id = trustedUser.id,
                userName = trustedUser.userName,
                ip = trustedUser.ip,
                createdTime = LocalDateTime.now(),
            )
        }
    }
}