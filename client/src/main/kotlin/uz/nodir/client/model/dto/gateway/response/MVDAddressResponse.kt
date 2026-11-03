package uz.nodir.client.model.dto.gateway.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import uz.repay.common.utils.serializer.DateDefaultDeSerializer
import java.time.LocalDate


@JsonIgnoreProperties(ignoreUnknown = true)
data class MVDAddressResponse(
    @JsonProperty("Data")
    val data: MVDAddressData? = null,

    @JsonProperty("AnswereId")
    val answerId: Int? = null,

    @JsonProperty("AnswereMessage")
    val answerMessage: String? = null,

    @JsonProperty("AnswereComment")
    val answerComment: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MVDAddressData(
    @JsonProperty("PermanentRegistration")
    val permanentRegistration: MVDBaseRegistration? = null,

    @JsonProperty("TemproaryRegistrations")
    val temporaryRegistration: List<MVDBaseRegistration>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MVDBaseRegistration(
    @JsonProperty("Cadastre")
    val cadastre: String? = null,

    @JsonProperty("Country")
    val country: MVDAddressBaseResult? = null,

    @JsonProperty("Region")
    val region: MVDAddressBaseResult? = null,

    @JsonProperty("District")
    val district: MVDAddressBaseResult? = null,

    @JsonProperty("Maxalla")
    val neighborhood: MVDAddressBaseResult? = null,

    @JsonProperty("Street")
    val street: MVDAddressBaseResult? = null,

    @JsonProperty("Address")
    val address: String? = null,

    @JsonProperty("RegistrationDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    val registrationDate: LocalDate? = null,

    @JsonProperty("DateFrom")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    val dateFrom: LocalDate? = null,

    @JsonProperty("DateTill")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    val dateTill: LocalDate? = null,
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class MVDAddressBaseResult(
    @JsonProperty("Id")
    val id: Int? = null,

    @JsonProperty("Value")
    val value: String? = null,

    @JsonProperty("IdValue")
    val idValue: String? = null,
)