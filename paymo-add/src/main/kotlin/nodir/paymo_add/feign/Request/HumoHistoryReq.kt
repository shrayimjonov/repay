package nodir.paymo_add.feign.Request

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import nodir.paymo_add.feign.humoware.Response.HumoHistoryResp
import java.util.ArrayList


class HumoHistoryReq(
    var id: String? = null,
    var params: Params? = null
) {


    class Params(
        var pan: String,
        var dateFrom: String,
        var dateTo: String


    ) {
        override fun toString(): String {
            return "Params(pan='$pan', dateFrom='$dateFrom', dateTo='$dateTo')"
        }

    }

    override fun toString(): String {
        return "HumoHistoryReq(id=$id, params=$params)"
    }

}