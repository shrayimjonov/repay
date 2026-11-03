package uz.nodir.client.model.dto.gateway.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonInclude(JsonInclude.Include.NON_NULL)
data class MVDAddressRequest(
    @JsonProperty("pinpp")
    val personCode: String
)