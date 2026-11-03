package uz.nodir.user.model.dto.userProtocol.response

import com.fasterxml.jackson.annotation.JsonProperty
import uz.nodir.user.model.entity.UserProtocol
import java.util.*

data class UserProtocolResponseDTO(

    @JsonProperty("id")
    val id: Long? = null,

    @JsonProperty("user_id")
    val userId: String? = null,

    @JsonProperty("user_name")
    val userName: String? = null,

    @JsonProperty("first_name")
    val firstName: String? = null,

    @JsonProperty("last_name")
    val lastName: String? = null,

    @JsonProperty("ip_address")
    val ipAddress: String? = null,

    @JsonProperty("created_time")
    val createdTime: String? = null,
) {
    companion object {
        fun from(userProtocol: UserProtocol): UserProtocolResponseDTO {
            return UserProtocolResponseDTO(
                id = userProtocol.id,
                userName = userProtocol.userName,
                userId = userProtocol.userId,
                firstName = userProtocol.firstName,
                lastName = userProtocol.lastName,
                ipAddress = userProtocol.ipAddress,
                createdTime = userProtocol.createdTime,
            )
        }
    }

}