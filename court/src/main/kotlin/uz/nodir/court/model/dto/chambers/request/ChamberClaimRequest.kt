package uz.nodir.court.model.dto.chambers.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Transient
import uz.nodir.court.model.dto.application.request.ApplicationCreateDocumentParam
import uz.nodir.court.model.dto.application.request.ApplicationCreateRequestDTO
import uz.nodir.court.model.dto.application.request.ApplicationPersonParams
import uz.repay.common.model.dto.response.file.FileSaveResponseDTO
import uz.repay.common.model.enums.Branch
import uz.repay.common.utils.BranchUtils



data class ChamberClaimRequest(
    @JsonProperty("organizationId")
    val organizationId: Int? = null,

    @JsonProperty("contractIdentificationNumber")
    val contractIdentificationNumber: String? = null,

    @JsonProperty("document")
    val document: Document? = null,
) {
    companion object {
        fun from(requestDTO: ApplicationCreateRequestDTO, extId: String): ChamberClaimRequest {
            return ChamberClaimRequest(
                organizationId = requestDTO.organizationId,
                contractIdentificationNumber = extId,
                document = Document.from(requestDTO.document, BranchUtils.getBranchByLoan(requestDTO.loanExtId))
            )
        }
    }

    fun toMap(): Map<Any, Any> {
        val map = mutableMapOf<Any, Any>()
        organizationId?.let { map["organizationId"] = it }
        contractIdentificationNumber?.let { map["contractIdentificationNumber"] = it }
        document?.let { map["document"] = it }
        return map
    }

}

data class Document(
    @JsonProperty("claimApplicationTypeId")
    val claimApplicationTypeId: Int? = null,

    @JsonProperty("claimThemeId")
    val claimThemeId: Int? = null,

    @JsonProperty("details")
    val details: String? = null,

    @JsonProperty("mainDebt")
    val mainDebt: Long? = null,

    @JsonProperty("calculedPenalty")
    val calculatedPenalty: Long? = null,

    @JsonProperty("penalty")
    val penalty: Long? = null,

    @JsonProperty("percent")
    val percent: Long? = null,

    @JsonProperty("currentPrincipalInterest")
    val currentPrincipalInterest: Long? = null,

    @JsonProperty("currentInterestRate")
    val currentInterestRate: Long? = null,

    @JsonProperty("otherDebtRepayment")
    val otherDebtRepayment: Long? = null,

    @JsonProperty("currencyId")
    val currencyId: Int? = null,

    @JsonProperty("bankBranchName")
    val bankBranchName: String? = null,

    @JsonProperty("bankResponsiblePerson")
    val bankResponsiblePerson: String? = null,

    @JsonProperty("files")
    val files: List<FileData>? = null,

    @JsonProperty("tables")
    val tables: List<TableData>? = null,
) {
    companion object {
        fun from(document: ApplicationCreateDocumentParam, branch: Branch): Document {
            return Document(
                claimApplicationTypeId = document.claimApplicationTypeId,
                claimThemeId = document.themeId,
                details = document.details,
                mainDebt = document.debtParams.mainDebt,
                calculatedPenalty = document.debtParams.calculatedPenalty,
                penalty = document.debtParams.penalty,
                percent = document.debtParams.percent,
                currentPrincipalInterest = document.debtParams.currentPrincipalInterest,
                currentInterestRate = document.debtParams.currentInterestRate,
                otherDebtRepayment = document.debtParams.otherDebtRepayment,
                currencyId = document.currencyId,
                bankBranchName = branch.getName(),
                bankResponsiblePerson = document.responsiblePerson,
                files = document.fileParams?.map(FileData::from) ?: listOf(),
                tables = document.personParams.map(TableData::from),
            )
        }
    }
}

data class FileData(
    @JsonProperty("fileAsBase64")
    @field:Transient
    val fileAsBase64: String? = null,

    @JsonProperty("FileExtension")
    val fileExtension: String? = null,
) {
    companion object {
        fun from(file: FileSaveResponseDTO) =
            FileData(
                fileAsBase64 = file.base64,
                fileExtension = ".${file.extension}"
            )
    }

    override fun toString(): String {
        return "FileData(fileExtension=$fileExtension)"
    }
}


data class TableData(
    @JsonProperty("claimResponsibleTypeId")
    val claimResponsibleTypeId: Int? = null,

    @JsonProperty("innOrPinfl")
    val personCode: String? = null,

    @JsonProperty("fullName")
    val fullName: String? = null,

    @JsonProperty("address")
    val address: String? = null,

    @JsonProperty("phoneNumber")
    val phone: String? = null
) {
    companion object {
        fun from(personData: ApplicationPersonParams) =
            TableData(
                claimResponsibleTypeId = personData.responsibleTypeId,
                personCode = personData.personCode,
                fullName = personData.fullName,
                address = personData.address,
                phone = personData.phone,
            )
    }
}