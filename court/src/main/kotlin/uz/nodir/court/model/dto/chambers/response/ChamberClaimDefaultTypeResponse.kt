package uz.nodir.court.model.dto.chambers.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import uz.nodir.court.model.dto.chambers.response.result.ChamberClaimTypeBaseResult


@JsonIgnoreProperties(ignoreUnknown = true)
data class ChamberClaimDefaultTypeResponse(
    @JsonProperty("value")
    override val value: Int? = null,

    @JsonProperty("text")
    override val text: String? = null,

    @JsonProperty("orderCode")
    override val orderCode: String? = null,
) : ChamberClaimTypeBaseResult()