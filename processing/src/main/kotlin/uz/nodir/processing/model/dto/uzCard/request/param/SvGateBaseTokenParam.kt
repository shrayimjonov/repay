package uz.nodir.processing.model.dto.uzCard.request.param

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
open class SvGateBaseTokenParam(
    @JsonProperty("ids")
    open val tokenList: List<String> = listOf()
)