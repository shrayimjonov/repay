package uz.nodir.court.model.dto.court.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import uz.repay.common.model.dto.request.core.params.PageParams
import uz.repay.common.model.dto.response.annotations.LoanId


data class CourtFilterRequestDTO(

    @JsonProperty("params")
    @field:Schema(
        name = "params",
        example = "{\n" +
                "        \"loan_ext_id\": \"00981273786\"\n" +
                "    }",
        description = "filter params",
        requiredMode = Schema.RequiredMode.REQUIRED,
        pattern = "\\d{9,}"
    )
    val params: CourtFilterParam
) : PageParams()

data class CourtFilterParam(
    @JsonProperty("loan_ext_id")
    @field:Schema(
        name = "loan_ext_id",
        example = "01121158076",
        description = "loan external id",
        requiredMode = Schema.RequiredMode.REQUIRED,
        pattern = "\\d{9,}"
    )
    @field:LoanId
    val loanExtId: String,
)