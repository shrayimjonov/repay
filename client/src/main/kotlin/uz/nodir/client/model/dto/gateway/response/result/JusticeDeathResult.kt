package uz.nodir.client.model.dto.gateway.response.result

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import uz.nodir.client.util.JusticeGenderConverter
import uz.repay.common.model.enums.Sex
import uz.repay.common.utils.serializer.DateDefaultDeSerializer
import java.time.LocalDate


@JsonIgnoreProperties(ignoreUnknown = true)
data class JusticeDeathResult(
    @JsonProperty("doc_num") val docNum: String? = null,
    @JsonProperty("doc_date") val docDate: String? = null,
    @JsonProperty("branch") val branch: String? = null,
    @JsonProperty("cert_series") val certSeries: String? = null,
    @JsonProperty("cert_number") val certNumber: String? = null,
    @JsonProperty("cert_death_date") val certDeathDate: String? = null,
    @JsonProperty("pnfl") val personCode: String? = null,
    @JsonProperty("surname") val surname: String? = null,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("patronym") val patronym: String? = null,

    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    @JsonFormat(pattern = "dd.MM.yyyy")
    @JsonProperty("birth_date") val birthDate: LocalDate? = null,

    @JsonProperty("death_place") val deathPlace: String? = null,
    @JsonProperty("birth_place") val birthPlace: String? = null,
    @JsonProperty("citizen_country") val citizenCountry: String? = null,

    @JsonDeserialize(converter = JusticeGenderConverter::class)
    @JsonProperty("gender_code") val genderCode: Sex? = null,

    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    @JsonFormat(pattern = "dd.MM.yyyy")
    @JsonProperty("death_date")
    val deathDate: LocalDate? = null,

    @JsonProperty("row_id") val rowId: String? = null,
) {
}