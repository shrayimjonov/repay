package uz.nodir.court.model.dto.application.response

import com.fasterxml.jackson.annotation.JsonProperty
import uz.nodir.court.model.dto.nciGateway.response.NCIGatewayLoanInfoChambersResponse
import uz.repay.common.utils.TextUtils
import java.util.*


data class ApplicationLoanInfoResponseDTO(

    @JsonProperty("currency")
    val currency: Int? = null,

    @JsonProperty("full_name")
    val fullName: String? = null,

    @JsonProperty("address")
    val address: String? = null,

    /*

    3) joriy davr uchun asosiy qarz bo‘yicha qarzdorlik

     */

    @JsonProperty("penalty")
    val penalty: Long? = 0,

    /*

    1) asosiy qarz bo‘yicha muddati o‘tgan qarzdorlik

     */

    @JsonProperty("main_debt")
    val mainDebt: Long? = 0,

    /*

    2) muddati o‘tgan foiz to‘lovi qarzdorligi

     */

    @JsonProperty("calculated_penalty")
    val calculatedPenalty: Long? = 0,

    /*

    5) muddati o‘tgan asosiy qarzga hisoblangan foiz qarzdorligi

     */

    @JsonProperty("current_principal_interest")
    val currentPrincipalInterest: Long? = 0,

    /*

    6) joriy davr uchun hisoblangan foiz qarzdorligi

     */
    @JsonProperty("current_interest_rate")
    val currentInterestRate: Long? = 0,


    @JsonProperty("branch")
    val branch: String? = null,

    @JsonProperty("client_id")
    val clientId: String? = null,

    @JsonProperty("person_code")
    val personCode: String? = null,

    ) {

    companion object {
        fun from(response: NCIGatewayLoanInfoChambersResponse, currency: Int) =
            ApplicationLoanInfoResponseDTO(
                currency = currency,
                clientId = response.clientId,
                branch = response.branch,
                fullName = response.fullName,
                personCode = response.personCode,
                address = TextUtils.cyrillicToLatin("${response.country} ${response.region} ${response.district} ${response.postAddress}")
                    .uppercase(),
                penalty = response.type2,
                mainDebt = response.type3 + response.type72,
                calculatedPenalty = response.type13 + response.type31,
                currentPrincipalInterest = response.type33 + response.type24,
                currentInterestRate = response.type4
            )
    }
}