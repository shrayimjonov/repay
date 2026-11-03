package nodir.paymo_add.feign.Request

class HumoHistoryRequestV2(
    var pan: String? = null,
    var beginDate: String? = null,
    var finishDate: String? = null,
    var subSystem: String? = null
) {
    override fun toString(): String {
        return "HumoHistoryRequestV2(pan=$pan, beginDate=$beginDate, finishDate=$finishDate, subSystem=$subSystem)"
    }
}