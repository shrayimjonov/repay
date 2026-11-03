package uz.nodir.client.model.dto.gateway.response.result

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import uz.repay.common.utils.converter.Int2BooleanConverter
import uz.repay.common.utils.serializer.DateDefaultDeSerializer
import java.time.LocalDate


@JsonIgnoreProperties(ignoreUnknown = true)
data class JusticeMarriageResult (
    @JsonProperty("doc_num") val docNum: String? = null,

    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    @JsonFormat(pattern = "dd.MM.yyyy")
    @JsonProperty("doc_date") val docDate: LocalDate? = null,

    @JsonProperty("branch") val branch: String? = null,
    @JsonProperty("cert_series") val certSeries: String? = null,
    @JsonProperty("cert_number") val certNumber: String? = null,

    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    @JsonFormat(pattern = "dd.MM.yyyy")
    @JsonProperty("cert_date") val certDate: LocalDate? = null,

    @JsonProperty("h_family") val husbandFamily: String? = null,
    @JsonProperty("h_family_after") val husbandFamilyAfter: String? = null,
    @JsonProperty("h_first_name") val husbandFirstName: String? = null,
    @JsonProperty("h_patronym") val husbandPatronym: String? = null,

    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    @JsonFormat(pattern = "dd.MM.yyyy")
    @JsonProperty("h_birth_day") val husbandBirthDay: LocalDate? = null,

    @JsonProperty("h_address") val husbandAddress: String? = null,
    @JsonProperty("h_sitizen") val husbandCitizen: String? = null,
    @JsonProperty("h_pnfl") val husbandPersonCode: String? = null,
    @JsonProperty("w_family") val wifeFamily: String? = null,
    @JsonProperty("w_family_after") val wifeFamilyAfter: String? = null,
    @JsonProperty("w_first_name") val wifeFirstName: String? = null,
    @JsonProperty("w_patronym") val wifePatronym: String? = null,

    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    @JsonFormat(pattern = "dd.MM.yyyy")
    @JsonProperty("w_birth_day") val wifeBirthDay: LocalDate? = null,

    @JsonProperty("w_address") val wifeAddress: String? = null,
    @JsonProperty("w_sitizen") val wifeCitizen: String? = null,
    @JsonProperty("w_pnfl") val wifePersonCode: String? = null,
    @JsonProperty("notes") val notes: String? = null,


    @JsonProperty("devorced")
    @JsonDeserialize(converter = Int2BooleanConverter::class)
    val divorced: Boolean? = null,


    @JsonProperty("repeal")
    @JsonDeserialize(converter = Int2BooleanConverter::class)
    val repeal: Boolean? = null,
)