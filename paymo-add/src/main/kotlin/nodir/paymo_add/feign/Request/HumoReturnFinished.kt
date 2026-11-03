package nodir.paymo_add.feign.Request

data class HumoReturnFinished (
    var paymentId: String,
    var merchantId: String,
    var terminalId: String,
    var login: String,
    var subSystem: String
)