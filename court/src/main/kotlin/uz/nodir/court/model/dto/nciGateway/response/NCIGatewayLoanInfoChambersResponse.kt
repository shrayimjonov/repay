package uz.nodir.court.model.dto.nciGateway.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
data class NCIGatewayLoanInfoChambersResponse(
    @JsonProperty("country")
    val country: String? = null,

    @JsonProperty("currency")
    val currency: String? = null,

    @JsonProperty("full_name")
    val fullName: String? = null,

    @JsonProperty("post_address")
    val postAddress: String? = null,

    @JsonProperty("type_2")
    val type2: Long = 0,

    @JsonProperty("type_3")
    val type3: Long = 0,

    @JsonProperty("type_4")
    val type4: Long = 0,

    @JsonProperty("type_13")
    val type13: Long = 0,

    @JsonProperty("type_24")
    val type24: Long = 0,

    @JsonProperty("type_31")
    val type31: Long = 0,

    @JsonProperty("type_33")
    val type33: Long = 0,

    @JsonProperty("type_72")
    val type72: Long = 0,

    @JsonProperty("type_133")
    val type133: Long = 0,

    @JsonProperty("branch")
    val branch: String? = null,

    @JsonProperty("client_id")
    val clientId: String? = null,

    @JsonProperty("person_code")
    val personCode: String? = null,

    @JsonProperty("district")
    val district: String? = null,

    @JsonProperty("region")
    val region: String? = null
)