package nodir.paymo_add.feign.Request

data class HumoExternalReq(
    var refNum: String,
    var login: String,
    var subSystem: String
)