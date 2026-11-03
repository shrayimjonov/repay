package uz.nodir.client.model.dto.justice.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import uz.nodir.client.model.dto.gateway.response.result.JusticeDeathResult
import uz.repay.common.model.enums.Sex
import java.time.LocalDate


@JsonIgnoreProperties(ignoreUnknown = true)
data class JusticeDeathResponseDTO(
    @JsonProperty("doc_num") var docNum: String? = null,
    @JsonProperty("doc_date") var docDate: String? = null,
    @JsonProperty("branch") var branch: String? = null,
    @JsonProperty("cert_series") var certSeries: String? = null,
    @JsonProperty("cert_number") var certNumber: String? = null,
    @JsonProperty("cert_death_date") var certDeathDate: String? = null,
    @JsonProperty("person_code") var personCode: String? = null,
    @JsonProperty("surname") var surname: String? = null,
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("patronymic") var patronymic: String? = null,
    @JsonProperty("birth_date") var birthDate: LocalDate? = null,
    @JsonProperty("death_place") var deathPlace: String? = null,
    @JsonProperty("birth_place") var birthPlace: String? = null,
    @JsonProperty("citizen_country") var citizenCountry: String? = null,
    @JsonProperty("gender_code") var genderCode: Sex? = null,
    @JsonProperty("death_date") var deathDate: LocalDate? = null,
    @JsonProperty("row_id") var rowId: String? = null,

    ) {
    constructor(justiceDeathResult: JusticeDeathResult?) : this() {
        this.docNum = justiceDeathResult?.docNum
        this.docDate = justiceDeathResult?.docDate
        this.branch = justiceDeathResult?.branch
        this.certSeries = justiceDeathResult?.certSeries
        this.certNumber = justiceDeathResult?.certNumber
        this.personCode = justiceDeathResult?.personCode
        this.surname = justiceDeathResult?.surname
        this.name = justiceDeathResult?.name
        this.patronymic = justiceDeathResult?.patronym
        this.birthDate = justiceDeathResult?.birthDate
        this.deathPlace = justiceDeathResult?.deathPlace
        this.birthPlace = justiceDeathResult?.birthPlace
        this.citizenCountry = justiceDeathResult?.citizenCountry
        this.genderCode = justiceDeathResult?.genderCode
        this.deathDate = justiceDeathResult?.deathDate
        this.rowId = justiceDeathResult?.rowId
    }
}