package uz.nodir.court.model.dto.nciGateway.request

import com.fasterxml.jackson.annotation.JsonProperty



data class NCIGatewayLoanInfoChambersRequest(
    @JsonProperty("loanExtId")
    val loanExtId: String,
)