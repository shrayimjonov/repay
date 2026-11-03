package uz.nodir.client.model.dto.gateway.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
data class JusticeBaseResponse<R>(
    @JsonProperty("id") val id: String,
    @JsonProperty("result_code") val resultCode: Int,
    @JsonProperty("result_message") val resultMessage: String,
    @JsonProperty("items") val items: R? = null
)
