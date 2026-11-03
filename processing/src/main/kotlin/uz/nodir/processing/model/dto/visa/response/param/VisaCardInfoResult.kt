package uz.nodir.processing.model.dto.visa.response.param

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
data class VisaCardInfoResult(
    @JsonProperty("id")
    var id: String? = null,

    @JsonProperty("username")
    var username: String? = null,

    @JsonProperty("pan")
    var pan: String? = null,

    @JsonProperty("expiry")
    var expiry: String? = null,

    @JsonProperty("status")
    var status: Int? = null,

    @JsonProperty("phone")
    var phone: String? = null,

    @JsonProperty("fullName")
    var fullName: String? = null,

    @JsonProperty("balance")
    var balance: Long? = null,

    @JsonProperty("sms")
    var sms: Boolean? = null,

    @JsonProperty("pincnt")
    var pinCount: Int? = null,

    @JsonProperty("cardtype")
    var cardType: String? = null,

    @JsonProperty("holdAmount")
    var holdAmount: Long? = null,

    @JsonProperty("cashbackAmount")
    var cashbackAmount: Long? = null,

    @JsonProperty("aacct")
    var account: String? = null,

    @JsonProperty("par")
    var par: String? = null
)