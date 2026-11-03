package nodir.paymo_add.feign.humoware.Response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.Data
import lombok.NoArgsConstructor
import java.util.*

@Data
class HumoHistoryResp {
    var id: String? = null

    @JsonProperty("result")
    var result: Result? = null

    @Data
    class Result {
        var rows: ArrayList<Row>? = null
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    class Row {
        @JsonProperty("REC_DATE")
        var recDate: String? = null

        @JsonProperty("CTIME")
        var cTime: String? = null

        @JsonProperty("TRAN_TYPE")
        var tranType: String? = null

        @JsonProperty("CARD")
        var card: String? = null

        @JsonProperty("AMOUNT_NET")
        var amountNet: String? = null

        @JsonProperty("POST_DATE")
        var postDate: String? = null

        @JsonProperty("TRAN_DATE_TIME")
        var tranDateTime: String? = null

        @JsonProperty("REF_NUMBER")
        var refNumber: String? = null

        @JsonProperty("TERM_ID")
        var termId: String? = null

        @JsonProperty("ABVR_NAME")
        var abvrName: String? = null

    }
}