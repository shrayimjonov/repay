package uz.davrbank.repay.loggingrest.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
@EnableConfigurationProperties(KeycloakPgProperties.class)
public class KeycloakTables {

    private final JdbcTemplate jdbc;
    private final KeycloakPgProperties properties;

    public KeycloakTables(JdbcTemplate jdbc, KeycloakPgProperties properties) {
        this.jdbc = jdbc;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createOrReplace() {
        if (!StringUtils.hasText(properties.getHost())
                || properties.getPort() <= 0
                || !StringUtils.hasText(properties.getDatabase())
                || !StringUtils.hasText(properties.getSchema())
                || !StringUtils.hasText(properties.getUsername())
                || properties.getPassword() == null) {
            log.warn("keycloak_user_protocol not created: repay.logging.keycloak-pg"
                    + " host, port, database, schema, username and password are all required");
            return;
        }
        try {
            jdbc.execute(userProtocolDdl());
            log.info("keycloak_user_protocol ready over {}:{}/{}.{}", properties.getHost(),
                    properties.getPort(), properties.getDatabase(), properties.getSchema());
        } catch (Exception e) {
            log.error("keycloak tables setup failed: {}", masked(e.getMessage()));
        }
    }

    String userProtocolDdl() {
        return "CREATE OR REPLACE TABLE keycloak_user_protocol ("
                + "user_id String, user_name String, first_name Nullable(String), "
                + "last_name Nullable(String), ip_address Nullable(String), "
                + "created_time DateTime64(6, 'Asia/Tashkent'))"
                + engine("user_protocol");
    }

    String masked(String message) {
        String password = properties.getPassword();
        if (message == null || password == null || password.isEmpty()) {
            return String.valueOf(message);
        }
        return message.replace(password, "******");
    }

    private String engine(String table) {
        return " ENGINE = PostgreSQL("
                + literal(properties.getHost() + ":" + properties.getPort()) + ", "
                + literal(properties.getDatabase()) + ", "
                + literal(table) + ", "
                + literal(properties.getUsername()) + ", "
                + literal(properties.getPassword()) + ", "
                + literal(properties.getSchema()) + ")";
    }

    private static String literal(String value) {
        return "'" + value.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }
}
