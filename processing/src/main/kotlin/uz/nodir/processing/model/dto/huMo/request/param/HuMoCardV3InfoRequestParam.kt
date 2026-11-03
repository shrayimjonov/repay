package uz.nodir.processing.model.dto.huMo.request.param

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonInclude(JsonInclude.Include.NON_NULL)
data class HuMoCardV3InfoRequestParam(
    @JsonProperty("primaryAccountNumber")
    val primaryAccountNumber: String,

    @JsonProperty("mb_flag")
    val mbFlag: Int = 1,
) {
    constructor(
        primaryAccountNumber: String,
    ) : this(
        primaryAccountNumber = primaryAccountNumber,
        mbFlag = 1,
    )
}