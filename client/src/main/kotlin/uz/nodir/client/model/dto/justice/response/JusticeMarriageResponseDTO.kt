package uz.nodir.client.model.dto.justice.response


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import uz.nodir.client.model.dto.gateway.response.result.JusticeMarriageResult

import java.time.LocalDate


@JsonIgnoreProperties(ignoreUnknown = true)
data class JusticeMarriageResponseDTO(
    @JsonProperty("doc_num") var docNum: String? = null,
    @JsonProperty("doc_date") var docDate: LocalDate? = null,
    @JsonProperty("branch") var branch: String? = null,
    @JsonProperty("cert_series") var certSeries: String? = null,
    @JsonProperty("cert_number") var certNumber: String? = null,
    @JsonProperty("cert_date") var certDate: LocalDate? = null,
    @JsonProperty("husband_family") var husbandFamily: String? = null,
    @JsonProperty("husband_family_after") var husbandFamilyAfter: String? = null,
    @JsonProperty("husband_first_name") var husbandFirstName: String? = null,
    @JsonProperty("husband_patronymic") var husbandPatronymic: String? = null,
    @JsonProperty("husband_birth_day") var husbandBirthDay: LocalDate? = null,
    @JsonProperty("husband_address") var husbandAddress: String? = null,
    @JsonProperty("husband_citizen") var husbandCitizen: String? = null,
    @JsonProperty("husband_person_code") var husbandPersonCode: String? = null,
    @JsonProperty("wife_family") var wifeFamily: String? = null,
    @JsonProperty("wife_family_after") var wifeFamilyAfter: String? = null,
    @JsonProperty("wife_first_name") var wifeFirstName: String? = null,
    @JsonProperty("wife_patronym") var wifePatronym: String? = null,
    @JsonProperty("wife_birth_day") var wifeBirthDay: LocalDate? = null,
    @JsonProperty("wife_address") var wifeAddress: String? = null,
    @JsonProperty("wife_citizen") var wifeCitizen: String? = null,
    @JsonProperty("wife_person_code") var wifePersonCode: String? = null,
    @JsonProperty("notes") var notes: String? = null,
    @JsonProperty("divorced") var divorced: Boolean? = null,
    @JsonProperty("repeal") var repeal: Boolean? = null,

    ) {
    constructor(justiceMarriageResult: JusticeMarriageResult?) : this() {
        this.docNum = justiceMarriageResult?.docNum
        this.docDate = justiceMarriageResult?.docDate
        this.branch = justiceMarriageResult?.branch
        this.certSeries = justiceMarriageResult?.certSeries
        this.certNumber = justiceMarriageResult?.certNumber
        this.certDate = justiceMarriageResult?.certDate
        this.husbandFamily = justiceMarriageResult?.husbandFamily
        this.husbandFamilyAfter = justiceMarriageResult?.husbandFamilyAfter
        this.husbandFirstName = justiceMarriageResult?.husbandFirstName
        this.husbandPatronymic = justiceMarriageResult?.husbandPatronym
        this.husbandBirthDay = justiceMarriageResult?.husbandBirthDay
        this.husbandAddress = justiceMarriageResult?.husbandAddress
        this.husbandCitizen = justiceMarriageResult?.husbandCitizen
        this.husbandPersonCode = justiceMarriageResult?.husbandPersonCode
        this.wifeFamily = justiceMarriageResult?.wifeFamily
        this.wifeFamilyAfter = justiceMarriageResult?.wifeFamilyAfter
        this.wifeFirstName = justiceMarriageResult?.wifeFirstName
        this.wifePatronym = justiceMarriageResult?.wifePatronym
        this.wifeBirthDay = justiceMarriageResult?.wifeBirthDay
        this.wifeAddress = justiceMarriageResult?.wifeAddress
        this.wifeCitizen = justiceMarriageResult?.wifeCitizen
        this.wifePersonCode = justiceMarriageResult?.wifePersonCode
        this.notes = justiceMarriageResult?.notes
        this.divorced = justiceMarriageResult?.divorced
        this.repeal = justiceMarriageResult?.repeal
    }
}