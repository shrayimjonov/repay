package uz.nodir.client.model.dto.gcp.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import uz.repay.common.utils.serializer.DateDefaultSerializer
import java.time.LocalDate


@JsonInclude(JsonInclude.Include.NON_NULL)
class GatewayClientInfoGCPRequest(@field:JsonProperty("sender_pinfl") var personCode: String, birthday: LocalDate) {
    @JsonProperty("is_consent")
    var isConsent: String = "Y"

    @JsonProperty("langId")
    var languageId: Int = 1 // 1= RU 2=UZ

    @JsonProperty("pinpp")
    var personCode2: String = personCode

    @JsonProperty("birth_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = DateDefaultSerializer::class)
    var birthDate: LocalDate = birthday

    @JsonProperty("is_photo")
    var isPhoto: String = "N" // N - No, Y - Yes

    @JsonProperty("Sender")
    var sender: String = "P"
}
