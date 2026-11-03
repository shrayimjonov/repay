package uz.nodir.client.model.dto.gateway.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
data class GatewayAuthResponse(
    @JsonProperty("access_token")
    val accessToken: String? = null,

    @JsonProperty("refresh_token")
    val refreshToken: String? = null,

    @JsonProperty("scope")
    val scope: String? = null,

    @JsonProperty("token_type")
    val tokenType: String? = null,

    @JsonProperty("expires_in")
    val expiresIn: Int? = null

)
