package uz.nodir.client.model.dto.gateway.request

import com.fasterxml.jackson.annotation.JsonProperty



data class YHXBBPersonCarRequest(
    @JsonProperty("pPinpp") val personCode: String
)