package uz.nodir.processing.model.dto.uzCard.request.param

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonInclude(JsonInclude.Include.NON_NULL)
data class SvGateBalanceParam(
    @JsonProperty("ids")
    override val tokenList: List<String> = listOf(),
) : SvGateBaseTokenParam(tokenList)