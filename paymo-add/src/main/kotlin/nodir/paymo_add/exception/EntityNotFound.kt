package nodir.paymo_add.exception

class EntityNotFound(message: String) : RuntimeException("entity with id $message not found") {
}