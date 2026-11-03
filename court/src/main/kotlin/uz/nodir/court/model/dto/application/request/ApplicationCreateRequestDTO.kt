package uz.nodir.court.model.dto.application.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import uz.repay.common.model.dto.response.annotations.LoanId
import uz.repay.common.model.dto.response.annotations.PersonCode
import uz.repay.common.model.dto.response.annotations.Phone
import uz.repay.common.model.dto.response.file.FileSaveResponseDTO



data class ApplicationCreateRequestDTO(

    @field:Min(1)
    @field:NotNull
    @JsonProperty("organization_id")
    val organizationId: Int,

    @field:LoanId
    @JsonProperty("loan_ext_id")
    val loanExtId: String,

    @field:Valid
    @field:NotNull
    @JsonProperty("document")
    val document: ApplicationCreateDocumentParam,
) {
    fun toMap(): Map<Any, Any> {
        return mapOf(
            "organization_id" to this.organizationId,
            "loan_ext_id" to this.loanExtId,
            "document" to this.document,
        )
    }

}


data class ApplicationCreateDocumentParam(

    @field:Min(1)
    @field:NotNull
    @JsonProperty("application_type_id")
    val claimApplicationTypeId: Int,

    @field:Min(1)
    @field:NotNull
    @JsonProperty("theme_id")
    val themeId: Int,

    @field:Min(1)
    @field:NotNull
    @JsonProperty("currency_id")
    val currencyId: Int,

    @field:NotBlank
    @JsonProperty("details")
    val details: String,

    @field:NotBlank
    @JsonProperty("responsible_person")
    val responsiblePerson: String,

    @field:Valid
    @field:NotNull
    @JsonProperty("debt_params")
    val debtParams: ApplicationDebtParam,


    @JsonProperty("file_params")
    val fileParams: List<FileSaveResponseDTO>? = null,

    @field:Valid
    @field:NotNull
    @field:NotEmpty
    @JsonProperty("person_params")
    val personParams: List<ApplicationPersonParams>,
)

data class ApplicationDebtParam(

   // @field:Min(1)
    @field:NotNull
    @JsonProperty("main_debt")
    val mainDebt: Long,


    @field:NotNull
    @JsonProperty("calculated_penalty")
    val calculatedPenalty: Long,


    @field:NotNull
    @JsonProperty("penalty")
    val penalty: Long,


    @field:NotNull
    @JsonProperty("percent")
    val percent: Long,


    @field:NotNull
    @JsonProperty("current_principal_interest")
    val currentPrincipalInterest: Long,


    @field:NotNull
    @JsonProperty("current_interest_rate")
    val currentInterestRate: Long,


    @field:NotNull
    @JsonProperty("other_debt_repayment")
    val otherDebtRepayment: Long,
)

data class ApplicationPersonParams(

    @field:Min(1)
    @field:NotNull
    @JsonProperty("responsible_type_id")
    val responsibleTypeId: Int,

    @field:PersonCode
    @JsonProperty("person_code")
    val personCode: String,

    @field:NotBlank
    @JsonProperty("full_name")
    val fullName: String,

    @field:NotBlank
    @JsonProperty("address")
    val address: String,

    @field:Phone
    @JsonProperty("phone")
    val phone: String,
)