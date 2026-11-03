package uz.nodir.processing.model.dto.master.response.result

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty



@JsonIgnoreProperties(ignoreUnknown = true)
data class MasterGetBalanceResult(
    @JsonProperty("pan")
    val pan: String? = null,

    @JsonProperty("token")
    val token: String? = null,

    @JsonProperty("username")
    val username: String? = null,

    @JsonProperty("status")
    val status: Int? = null,

    @JsonProperty("balance")
    val balance: Long? = null,

    @JsonProperty("phone")
    val phone: String? = null,

    @JsonProperty("holder")
    val holder: String? = null,

    @JsonProperty("pinCount")
    val pinCount: Int? = null,

    @JsonProperty("customer")
    val customer: String? = null,

    @JsonProperty("account")
    val account: String? = null,

    @JsonProperty("currency")
    val currency: String? = null
)