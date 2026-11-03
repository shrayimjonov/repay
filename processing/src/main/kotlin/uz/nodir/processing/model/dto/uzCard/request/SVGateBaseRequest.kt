package uz.nodir.processing.model.dto.uzCard.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import uz.nodir.processing.utils.SystemTool


@JsonInclude(JsonInclude.Include.NON_NULL)
data class SVGateBaseRequest<P>(
    @JsonProperty("jsonrpc")
    val jsonrpc: String = "2.0",
    @JsonProperty("method")
    val method: String? = null,
    @JsonProperty("id")
    val id: String? = SystemTool.requestIDGenerator(),
    @JsonProperty("params")
    val params: P? = null,
)