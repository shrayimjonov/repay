package uz.nodir.court.model.dto.chambers.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
class ChamberAuthResponse(
    @JsonProperty("token")
    val accessToken: String,
)