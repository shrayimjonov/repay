package uz.davrbank.repay.loggingrest.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class KeycloakTablesTest {

    @Mock
    private JdbcTemplate jdbc;

    private KeycloakPgProperties properties;
    private KeycloakTables tables;

    @BeforeEach
    void setUp() {
        properties = new KeycloakPgProperties();
        properties.setHost("127.0.0.1");
        properties.setPort(5432);
        properties.setDatabase("keycloak");
        properties.setSchema("public");
        properties.setUsername("kc_reader");
        properties.setPassword("s3cret");
        tables = new KeycloakTables(jdbc, properties);
    }

    @Test
    void should_create_or_replace_the_keycloak_tables_from_configured_credentials() {
        tables.createOrReplace();

        var captor = ArgumentCaptor.forClass(String.class);
        verify(jdbc, times(1)).execute(captor.capture());
        assertThat(captor.getAllValues()).containsExactly(tables.userProtocolDdl());
    }

    @Test
    void should_map_the_user_protocol_table() {
        assertThat(tables.userProtocolDdl())
                .startsWith("CREATE OR REPLACE TABLE keycloak_user_protocol (")
                .endsWith(" ENGINE = PostgreSQL('127.0.0.1:5432', 'keycloak', 'user_protocol', 'kc_reader', 's3cret', 'public')");
    }

    @Test
    void should_declare_the_columns_the_session_queries_read() {
        assertThat(tables.userProtocolDdl())
                .contains("user_id String")
                .contains("user_name String")
                .contains("first_name Nullable(String)")
                .contains("last_name Nullable(String)")
                .contains("ip_address Nullable(String)");
    }

    @Test
    void should_map_created_time_at_microsecond_scale_in_tashkent_time() {
        assertThat(tables.userProtocolDdl()).contains("created_time DateTime64(6, 'Asia/Tashkent')");
    }

    @Test
    void should_use_the_configured_location() {
        properties.setPort(6432);
        properties.setDatabase("kc");
        properties.setSchema("keycloak");

        assertThat(tables.userProtocolDdl()).endsWith("PostgreSQL('127.0.0.1:6432', 'kc', 'user_protocol', 'kc_reader', 's3cret', 'keycloak')");
    }

    @Test
    void should_escape_quotes_and_backslashes_in_credentials() {
        properties.setPassword("pa'ss\\word");

        assertThat(tables.userProtocolDdl()).contains(", 'pa\\'ss\\\\word', ");
    }

    @Test
    void should_skip_when_the_password_is_missing() {
        properties.setPassword(null);

        tables.createOrReplace();

        verifyNoInteractions(jdbc);
    }

    @Test
    void should_skip_when_the_username_is_blank() {
        properties.setUsername("  ");

        tables.createOrReplace();

        verifyNoInteractions(jdbc);
    }

    @Test
    void should_skip_when_the_host_is_missing() {
        properties.setHost(null);

        tables.createOrReplace();

        verifyNoInteractions(jdbc);
    }

    @Test
    void should_skip_when_the_port_is_not_set() {
        properties.setPort(0);

        tables.createOrReplace();

        verifyNoInteractions(jdbc);
    }

    @Test
    void should_skip_when_the_database_is_missing() {
        properties.setDatabase(null);

        tables.createOrReplace();

        verifyNoInteractions(jdbc);
    }

    @Test
    void should_skip_when_the_schema_is_blank() {
        properties.setSchema("");

        tables.createOrReplace();

        verifyNoInteractions(jdbc);
    }

    @Test
    void should_not_fail_startup_when_clickhouse_rejects_the_tables() {
        doThrow(new DataAccessResourceFailureException("Code: 497. Not enough privileges"))
                .when(jdbc).execute(anyString());

        assertThatCode(() -> tables.createOrReplace()).doesNotThrowAnyException();
    }

    @Test
    void should_mask_the_password_in_error_messages() {
        assertThat(tables.masked("auth failed for kc_reader:s3cret@127.0.0.1"))
                .isEqualTo("auth failed for kc_reader:******@127.0.0.1");
    }

    @Test
    void should_survive_an_error_without_a_message() {
        assertThat(tables.masked(null)).isEqualTo("null");
    }
}
