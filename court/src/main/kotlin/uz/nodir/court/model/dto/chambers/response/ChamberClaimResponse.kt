package uz.nodir.court.model.dto.chambers.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
class ChamberClaimResponse(
    @JsonProperty("id") val id: String? = null,
    @JsonProperty("contractIdentificationNumber") val contractIdentificationNumber: String? = null,
)