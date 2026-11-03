package uz.nodir.court.model.dto.application.response

import com.fasterxml.jackson.annotation.JsonProperty
import uz.nodir.court.model.dto.chambers.response.ChamberClaimCurrencyTypeResponse
import uz.nodir.court.model.dto.chambers.response.ChamberClaimDefaultTypeResponse
import uz.nodir.court.model.dto.chambers.response.ChamberClaimLanguageTypeResponse
import java.io.Serializable



data class ApplicationDataResponseDTO(
    @JsonProperty("application_types")
    val applicationTypes: List<ApplicationDefaultTypesResultDTO>? = null,

    @JsonProperty("claim_themes")
    val claimThemes: List<ApplicationDefaultTypesResultDTO>? = null,

    @JsonProperty("claim_responsible_types")
    val claimResponsibleTypes: List<ApplicationDefaultTypesResultDTO>? = null,

    @JsonProperty("currencies")
    val currencies: List<ApplicationCurrencyTypesResultDTO>? = null,

    @JsonProperty("organizations")
    val organizations: List<ApplicationDefaultTypesResultDTO>? = null,

    @JsonProperty("languages")
    val languages: List<ApplicationLanguageTypesResultDTO>? = null
): Serializable {
    companion object {

        fun from(
            appTypes: List<ChamberClaimDefaultTypeResponse>,
            themeTypes: List<ChamberClaimDefaultTypeResponse>,
            responsibleTypes: List<ChamberClaimDefaultTypeResponse>,
            currencyTypes: List<ChamberClaimCurrencyTypeResponse>,
            organizationTypes: List<ChamberClaimDefaultTypeResponse>
        ) =
            ApplicationDataResponseDTO(
                applicationTypes = appTypes.map(ApplicationDefaultTypesResultDTO.Companion::from),
                claimThemes = themeTypes.map(ApplicationDefaultTypesResultDTO.Companion::from),
                claimResponsibleTypes = responsibleTypes.map(ApplicationDefaultTypesResultDTO.Companion::from),
                currencies = currencyTypes.map(ApplicationCurrencyTypesResultDTO.Companion::from),
                organizations = organizationTypes.map(ApplicationDefaultTypesResultDTO.Companion::from)
            )


        fun from(
            appTypes: List<ChamberClaimDefaultTypeResponse>,
            themeTypes: List<ChamberClaimDefaultTypeResponse>,
            responsibleTypes: List<ChamberClaimDefaultTypeResponse>,
            currencyTypes: List<ChamberClaimCurrencyTypeResponse>,
            organizationTypes: List<ChamberClaimDefaultTypeResponse>,
            languages: List<ChamberClaimLanguageTypeResponse>,
        ) =
            ApplicationDataResponseDTO(
                applicationTypes = appTypes.map(ApplicationDefaultTypesResultDTO.Companion::from),
                claimThemes = themeTypes.map(ApplicationDefaultTypesResultDTO.Companion::from),
                claimResponsibleTypes = responsibleTypes.map(ApplicationDefaultTypesResultDTO.Companion::from),
                currencies = currencyTypes.map(ApplicationCurrencyTypesResultDTO.Companion::from),
                organizations = organizationTypes.map(ApplicationDefaultTypesResultDTO.Companion::from),
                languages = languages.map(ApplicationLanguageTypesResultDTO.Companion::from)
            )


    }
}

data class ApplicationDefaultTypesResultDTO(
    override val value: Int? = null,
    override val text: String? = null,
    override val orderCode: String? = null,
) : ApplicationBaseTypeResult(), Serializable {
    companion object {
        fun from(type: ChamberClaimDefaultTypeResponse) =
            ApplicationDefaultTypesResultDTO(
                value = type.value,
                text = type.text,
                orderCode = type.orderCode,
            )
    }
}

data class ApplicationCurrencyTypesResultDTO(
    @JsonProperty("currency_code")
    val currencyCode: String? = null,
    override val value: Int? = null,
    override val text: String? = null,
    override val orderCode: String? = null,
) : ApplicationBaseTypeResult(), Serializable {

    companion object {
        fun from(type: ChamberClaimCurrencyTypeResponse) =
            ApplicationCurrencyTypesResultDTO(
                value = type.value,
                text = type.text,
                orderCode = type.orderCode,
                currencyCode = type.currencyCode
            )
    }
}


data class ApplicationLanguageTypesResultDTO(
    @JsonProperty("code")
    val code: String? = null,

    @JsonProperty("short_name")
    val shortName: String? = null,

    @JsonProperty("full_name")
    val fullName: String? = null,

    override val value: Int? = null,
    override val text: String? = null,
    override val orderCode: String? = null,
) : ApplicationBaseTypeResult(), Serializable {

    companion object {
        fun from(type: ChamberClaimLanguageTypeResponse) =
            ApplicationLanguageTypesResultDTO(
                code = type.code,
                shortName = type.shortName,
                fullName = type.fullName,
                value = type.value,
                text = type.text,
                orderCode = type.orderCode,
            )
    }

}


abstract class ApplicationBaseTypeResult(
    @JsonProperty("value")
    open val value: Int? = null,

    @JsonProperty("text")
    open val text: String? = null,

    @JsonProperty("orderCode")
    open val orderCode: String? = null
)