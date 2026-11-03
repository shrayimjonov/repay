package uz.nodir.client.model.dto.gateway.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class JusticeDeathRequest(
    @JsonProperty(value = "pin") val personCode: String
)