package uz.davrbank.repay.loggingrest.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "repay.logging.keycloak-pg")
public class KeycloakPgProperties {

    private String host;

    private int port;

    private String database;

    private String schema;

    private String username;

    private String password;
}