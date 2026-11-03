package nodir.paymo_add.exception

class CardNotFound(message: String, status: String) : RuntimeException("Card with id $message is $status") {
}