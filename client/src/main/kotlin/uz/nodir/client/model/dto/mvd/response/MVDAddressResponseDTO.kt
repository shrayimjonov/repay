package uz.nodir.client.model.dto.mvd.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import uz.nodir.client.model.dto.gateway.response.MVDAddressBaseResult
import uz.nodir.client.model.dto.gateway.response.MVDAddressData
import uz.nodir.client.model.dto.gateway.response.MVDAddressResponse
import uz.nodir.client.model.dto.gateway.response.MVDBaseRegistration
import java.time.LocalDate


@JsonIgnoreProperties(ignoreUnknown = true)
data class MVDAddressResponseDTO(
    @JsonProperty("data")
    var data: MVDAddressDataDTO? = null,

    @JsonProperty("answer_id")
    var answerId: Int? = null,

    @JsonProperty("answer_message")
    var answerMessage: String? = null,

    @JsonProperty("answer_comment")
    var answerComment: String? = null,
) {
    constructor(response: MVDAddressResponse) : this() {
        response.data?.let {
            this.data = MVDAddressDataDTO(it)
        }

        this.answerId = response.answerId
        this.answerMessage = response.answerMessage
        this.answerComment = response.answerComment
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class MVDAddressDataDTO(
    @JsonProperty("permanent_registration")
    var permanentRegistration: MVDBaseRegistrationDTO? = null,

    @JsonProperty("temporary_registration")
    var temporaryRegistration: List<MVDBaseRegistrationDTO>? = null
) {
    constructor(addressData: MVDAddressData) : this() {
        addressData.permanentRegistration?.let {
            this.permanentRegistration = MVDBaseRegistrationDTO(addressData.permanentRegistration)
        }

        if (!addressData.temporaryRegistration.isNullOrEmpty()) {
            this.temporaryRegistration =
                addressData.temporaryRegistration
                    .map(::MVDBaseRegistrationDTO)
        }


    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class MVDBaseRegistrationDTO(
    @JsonProperty("cadastre")
    var cadastre: String? = null,

    @JsonProperty("country")
    var country: MVDAddressBaseResult? = null,

    @JsonProperty("region")
    var region: MVDAddressBaseResult? = null,

    @JsonProperty("district")
    var district: MVDAddressBaseResult? = null,

    @JsonProperty("neighborhood")
    var neighborhood: MVDAddressBaseResult? = null,

    @JsonProperty("street")
    var street: MVDAddressBaseResult? = null,

    @JsonProperty("address")
    var address: String? = null,

    @JsonProperty("registration_date")
    var registrationDate: LocalDate? = null,

    @JsonProperty("date_from")
    var dateFrom: LocalDate? = null,

    @JsonProperty("date_to")
    var dateTo: LocalDate? = null,
) {
    constructor(registration: MVDBaseRegistration?) : this() {
        this.cadastre = registration?.cadastre
        this.country = registration?.country
        this.region = registration?.region
        this.district = registration?.district
        this.neighborhood = registration?.neighborhood
        this.street = registration?.street
        this.address = registration?.address
        this.registrationDate = registration?.registrationDate
        this.dateFrom = registration?.dateFrom
        this.dateTo = registration?.dateTill
    }
}


@JsonIgnoreProperties(ignoreUnknown = true)
data class MVDAddressBaseResultDTO(
    @JsonProperty("id")
    val id: Int? = null,

    @JsonProperty("value")
    val value: String? = null,

    @JsonProperty("idValue")
    val idValue: String? = null,
)