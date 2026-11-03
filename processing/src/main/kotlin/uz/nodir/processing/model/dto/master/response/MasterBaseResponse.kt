package uz.nodir.processing.model.dto.master.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import uz.repay.common.model.dto.response.core.CoreError


@JsonIgnoreProperties(ignoreUnknown = true)
data class MasterBaseResponse<R>(
    @JsonProperty("successCode")
    val successCode: Int? = null,

    @JsonProperty("isSuccess")
    val isSuccess: Boolean? = false,

    @JsonProperty("status")
    val status: String? = null,

    @JsonProperty("result")
    val result: R? = null,

    @JsonProperty("error")
    val error: CoreError? = null

)