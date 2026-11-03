package uz.nodir.processing.model.dto.huMo.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import uz.nodir.processing.utils.SystemTool


@JsonInclude(JsonInclude.Include.NON_NULL)
data class HuMoMiddlewareBaseRequest<P>(
    @JsonProperty("id")
    val id: String = SystemTool.requestIDGenerator(),

    @JsonProperty("params")
    val params: P? = null,
)