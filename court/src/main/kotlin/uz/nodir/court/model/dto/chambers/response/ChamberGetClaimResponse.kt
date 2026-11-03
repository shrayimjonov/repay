package uz.nodir.court.model.dto.chambers.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
data class ChamberGetClaimResponse(
    @JsonProperty("claimApplicationStatus")
    val claimApplicationStatus: String? = null,

    @JsonProperty("mediationStatus")
    val mediationStatus: String? = null,

    @JsonProperty("mediationPlanStatus")
    val mediationPlanStatus: String? = null,

    @JsonProperty("applicationForCourtStatus")
    val applicationForCourtStatus: String? = null,

    @JsonProperty("claimApplicationIntegrationStatusId")
    val claimApplicationIntegrationStatusId: ClaimApplicationIntegrationStatusId? = null,

    @JsonProperty("claimApplicationIntegrationFileUrl")
    val claimApplicationIntegrationFileUrl: ClaimApplicationIntegrationFileUrl? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ClaimApplicationIntegrationStatusId(
    @JsonProperty("claimApplicationStatusId")
    val claimApplicationStatusId: Int? = null,

    @JsonProperty("mediationStatusId")
    val mediationStatusId: Int? = null,

    @JsonProperty("mediationPlanStatusId")
    val mediationPlanStatusId: Int? = null,

    @JsonProperty("applicationForCourtStatusId")
    val applicationForCourtStatusId: Int? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ClaimApplicationIntegrationFileUrl(
    @JsonProperty("claimApplicationLink")
    val claimApplicationLink: String? = null,

    @JsonProperty("mediationPlanLink")
    val mediationPlanLink: String? = null,

    @JsonProperty("mediationLink")
    val mediationLink: String? = null,

    @JsonProperty("applicationForCourtLink")
    val applicationForCourtLink: String? = null
)