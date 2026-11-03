package uz.nodir.court.model.dto.chambers.response.result

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
abstract class ChamberClaimTypeBaseResult(
    @JsonProperty("value")
    open val value: Int? = null,

    @JsonProperty("text")
    open val text: String? = null,

    @JsonProperty("orderCode")
    open val orderCode: String? = null
)