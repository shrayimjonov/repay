package uz.nodir.court.model.dto.court.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import uz.nodir.court.model.dto.court.request.param.CourtBaseParam
import uz.repay.common.model.dto.response.annotations.LoanId
import uz.repay.common.model.dto.response.annotations.Phone
import uz.repay.common.model.enums.Language

@Schema(allOf = [CourtBaseParam::class])
data class CreateLoanCourtRequestDTO(
    @JsonProperty("loan_ext_id")
    @field:Schema(
        name = "loan_ext_id",
        example = "01121158076",
        description = "loan external id",
        requiredMode = Schema.RequiredMode.REQUIRED,
        pattern = "\\d{9,}"
    )
    @field:LoanId val loanExtId: String,

    @JsonProperty("sent_date_time")
    override val sentDateTime: String,

    @JsonProperty("message")
    override val message: String,

    @JsonProperty("phone")
    override val phone: String,

    ) : CourtBaseParam(
    sentDateTime,
    message,
    phone
)