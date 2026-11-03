package nodir.paymo_add.feign.Request

data class HumoReverse(
    var paymentId: String,
    var login: String,
    var subSystem: String
)