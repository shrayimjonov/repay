package uz.nodir.processing.model.dto.master.request.param

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonInclude(JsonInclude.Include.NON_NULL)
data class MasterGetBalanceParam(
    @JsonProperty("token")
    val token: String
)