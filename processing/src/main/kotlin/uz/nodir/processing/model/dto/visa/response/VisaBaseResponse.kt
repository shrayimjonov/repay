package uz.nodir.processing.model.dto.visa.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import uz.repay.common.model.dto.response.core.CoreError


@JsonIgnoreProperties(ignoreUnknown = true)
data class VisaBaseResponse<R>(
    @JsonProperty("jsonrpc")
    val jsonRPC: String? = null,

    @JsonProperty("id")
    val id: String? = null,

    @JsonProperty("result")
    val result: R? = null,

    @JsonProperty("error")
    val error: CoreError? = null
)