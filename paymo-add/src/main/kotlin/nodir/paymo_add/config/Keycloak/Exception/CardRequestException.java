package nodir.paymo_add.config.Keycloak.Exception;

public class CardRequestException extends RuntimeException {

    public CardRequestException(Long id) {
        super(String.format("Entity with Id %d not found",id));
    }


}
