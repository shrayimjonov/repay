package uz.nodir.court.model.dto.application.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import uz.nodir.court.model.dto.chambers.request.TableData
import uz.nodir.court.model.entity.ChambersApplication
import uz.repay.common.model.enums.ResponseStatus
import uz.repay.common.utils.CommonUtils
import java.util.*


data class ApplicationResponseDTO(

    @JsonProperty("id")
    val id: UUID? = null,

    @JsonProperty("ext_id")
    val extId: String? = null,

    @JsonProperty("loan_ext_id")
    val loanExtId: String? = null,

    @JsonProperty("ip_address")
    val ipAddress: String? = null,

    @JsonProperty("request")
    val request: Map<Any, Any>? = null,

    @JsonProperty("response")
    val response: Map<Any, Any>? = null,

    @JsonProperty("status")
    val status: ResponseStatus? = null,

    @JsonProperty("file_ids")
    val fileIds: List<Long>? = listOf(),

    @JsonProperty("responsible_person")
    val responsiblePerson: String? = null,

    @JsonProperty("app_id")
    val appId: String? = null,

    @JsonProperty("sent_data")
    val sentData: ApplicationSentData? = null,
) {
    companion object {
        fun from(application: ChambersApplication) =
            ApplicationResponseDTO(
                id = application.id,
                extId = application.extId,
                loanExtId = application.loanExtId,
                ipAddress = application.ipAddress,
                request = application.request,
                response = application.response,
                status = application.status,
                fileIds = application.fileIds,
                responsiblePerson = application.responsiblePerson,
                appId = application.appId,
                sentData = ApplicationSentData.from(application.request)
            )
    }
}

data class ApplicationSentData(
    @JsonProperty("organization")
    val organization: Int? = null,

    @JsonProperty("theme")
    val theme: Int? = null,

    @JsonProperty("details")
    val details: String? = null,

    @JsonProperty("currency")
    val currency: Int? = null,

    @JsonProperty("branch")
    val branch: String? = null,

    @JsonProperty("responsible_person")
    val responsiblePerson: String? = null,

    @JsonProperty("application_type")
    val applicationType: Int? = null,

    @JsonProperty("debt_data")
    val debtData: ApplicationSentDebtData? = null,

    @JsonProperty("persons_data")
    val personsData: List<ApplicationSentPersonData>? = null,
) {
    companion object {

        @Suppress("UNCHECKED_CAST")
        fun from(request: Map<Any, Any>?): ApplicationSentData? {
            request?.let {
                val person = CommonUtils.getNested(request, "document", "tables") as List<Map<Any, Any>>?

                val tableDataList = person?.map { map ->
                    jacksonObjectMapper().convertValue(map, TableData::class.java)
                }

                return ApplicationSentData(
                    organization = request["organization_id"]?.toString()?.toInt(),
                    theme = CommonUtils.getNested(request, "document", "claimThemeId")?.toString()?.toInt(),
                    details = CommonUtils.getNested(request, "document", "details")?.toString(),
                    currency = CommonUtils.getNested(request, "document", "currencyId")?.toString()?.toInt(),
                    branch = CommonUtils.getNested(request, "document", "bankBranchName")?.toString(),
                    responsiblePerson = CommonUtils.getNested(request, "document", "bankResponsiblePerson")?.toString(),
                    applicationType = CommonUtils.getNested(request, "document", "claimApplicationTypeId")?.toString()?.toInt(),
                    debtData = ApplicationSentDebtData.from(request),
                    personsData = tableDataList?.map(ApplicationSentPersonData.Companion::from)
                )
            } ?: return null
        }
    }

}

data class ApplicationSentDebtData(
    @JsonProperty("main_debt")
    val mainDebt: Long? = null,

    @JsonProperty("calculated_penalty")
    val calculatedPenalty: Long? = null,

    @JsonProperty("penalty")
    val penalty: Long? = null,

    @JsonProperty("percent")
    val percent: Long? = null,

    @JsonProperty("current_principal_interest")
    val currentPrincipalInterest: Long? = null,

    @JsonProperty("current_interest_rate")
    val currentInterestRate: Long? = null,

    @JsonProperty("other_debt_repayment")
    val otherDebtRepayment: Long? = null,

    ) {
    companion object {
        fun from(request: Map<Any, Any>?): ApplicationSentDebtData =
            ApplicationSentDebtData(
                mainDebt = CommonUtils.getNested(request, "document", "mainDebt")?.toString()?.toLong(),
                calculatedPenalty = CommonUtils.getNested(request, "document", "calculedPenalty")?.toString()?.toLong(),
                penalty = CommonUtils.getNested(request, "document", "penalty")?.toString()?.toLong(),
                percent = CommonUtils.getNested(request, "document", "percent")?.toString()?.toLong(),
                currentPrincipalInterest = CommonUtils.getNested(request, "document", "currentPrincipalInterest")
                    ?.toString()?.toLong(),
                currentInterestRate = CommonUtils.getNested(request, "document", "currentInterestRate")?.toString()
                    ?.toLong(),
                otherDebtRepayment = CommonUtils.getNested(request, "document", "otherDebtRepayment")?.toString()
                    ?.toLong(),
            )
    }
}


data class ApplicationSentPersonData(
    @JsonProperty("responsible_type")
    val responsibleType: Int? = null,

    @JsonProperty("person_code")
    val personCode: String? = null,

    @JsonProperty("full_name")
    val fullName: String? = null,

    @JsonProperty("address")
    val address: String? = null,

    @JsonProperty("phone")
    val phone: String? = null,
) {
    companion object {
        fun from(data: TableData): ApplicationSentPersonData =
            ApplicationSentPersonData(
                responsibleType = data.claimResponsibleTypeId,
                personCode = data.personCode,
                fullName = data.fullName,
                address = data.address,
                phone = data.phone
            )
    }
}