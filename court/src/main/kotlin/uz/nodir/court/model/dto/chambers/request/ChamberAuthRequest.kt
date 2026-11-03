package uz.nodir.court.model.dto.chambers.request

import com.fasterxml.jackson.annotation.JsonProperty



data class ChamberAuthRequest(
    @JsonProperty("userName")
    val username: String,

    @JsonProperty("password")
    val password: String
)