package nodir.paymo_add.exception

class ConnectionError(message: String, answer: String?) : RuntimeException("$message dont answer, connection closed. $answer") {
}