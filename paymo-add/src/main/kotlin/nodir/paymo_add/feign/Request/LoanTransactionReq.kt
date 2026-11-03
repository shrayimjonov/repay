package nodir.paymo_add.feign.Request

data class LoanTransactionReq(
    var branch: String,
    var loanId: String,
    var dateFrom: String
)