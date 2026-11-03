package nodir.paymo_add.exception

class TransactionFinished(message: String, status: String) : RuntimeException("Transaction with id $message is $status") {
}