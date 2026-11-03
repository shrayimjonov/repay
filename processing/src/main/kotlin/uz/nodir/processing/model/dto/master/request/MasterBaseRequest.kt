package uz.nodir.processing.model.dto.master.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonInclude(JsonInclude.Include.NON_NULL)
data class MasterBaseRequest<P>(
    @JsonProperty("id")
    val id: String? = null,

    @JsonProperty("params")
    val params: P? = null
)