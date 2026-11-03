package uz.davrbank.repay.loggingrest.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uz.davrbank.repay.commonlogging.config.LoggingProperties;
import uz.davrbank.repay.loggingrest.dto.ActiveSession;
import uz.davrbank.repay.loggingrest.dto.ActiveSessionsRequest;

import java.sql.ResultSet;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApiLogRepositoryActiveSessionsTest {

    @Mock
    private JdbcTemplate jdbc;

    private LoggingProperties properties;
    private ApiLogRepository repository;

    @BeforeEach
    void setUp() {
        properties = new LoggingProperties();
        properties.getActiveSessions().setTtl(Duration.ofMinutes(5));
        repository = new ApiLogRepository(jdbc, properties);
        doReturn(1L).when(jdbc).queryForObject(anyString(), any(RowMapper.class), any(Object[].class));
        doReturn(List.of()).when(jdbc).query(anyString(), any(RowMapper.class), any(Object[].class));
    }

    @Test
    void should_build_one_login_row_per_user_and_login_ip() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql())
                .contains("SELECT user_name, ifNull(ip_address, '') AS login_ip,")
                .contains("FROM keycloak_user_protocol")
                .contains("max(created_time) AS login_time")
                .doesNotContain("keycloak_event")
                .doesNotContain("keycloak_user_attribute")
                .contains("GROUP BY user_name, login_ip");
    }

    @Test
    void should_look_back_for_logins_over_the_fifteen_minute_session_max() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql())
                .contains("FROM keycloak_user_protocol WHERE created_time >= now() - 900")
                .contains("WHERE event_time >= now() - 900");
        assertThat(countArgs()).isEmpty();
    }

    @Test
    void should_never_look_back_less_than_the_ttl() {
        properties.getActiveSessions().setTtl(Duration.ofMinutes(30));
        repository = new ApiLogRepository(jdbc, properties);

        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql()).contains("WHERE event_time >= now() - 1800");
    }

    @Test
    void should_count_only_successful_activity_made_after_the_login() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql())
                .contains("INNER JOIN pair_login ON pair_login.user_name = api_logs.username"
                        + " AND pair_login.login_ip = api_logs.client_ip")
                .contains("AND event_time >= login_time AND success AND username != ''");
    }

    @Test
    void should_group_sessions_by_username_and_ip() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql())
                .contains("SELECT pairs.username AS username, pairs.client_ip AS ip,")
                .contains("GROUP BY pairs.username, pairs.client_ip,");
    }

    @Test
    void should_list_the_login_ip_even_before_any_request() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql()).contains(" UNION ALL SELECT user_name AS username, login_ip AS client_ip,"
                + " login_time AS last_action, toUInt64(0) AS request_count FROM pair_login");
    }

    @Test
    void should_take_the_login_ip_from_the_user_protocol_row() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql())
                .contains("ifNull(ip_address, '') AS login_ip")
                .doesNotContain("first_ip")
                .doesNotContain("userIp");
    }

    @Test
    void should_give_each_login_ip_its_own_login_time() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql())
                .contains("pair_login.login_time AS login_time")
                .contains("FROM pairs INNER JOIN pair_login ON pair_login.user_name = pairs.username"
                        + " AND pair_login.login_ip = pairs.client_ip");
    }

    @Test
    void should_fall_back_to_the_latest_login_for_ips_that_never_logged_in() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql())
                .contains("max(login_time) AS last_login_time FROM logins GROUP BY user_name)")
                .contains("INNER JOIN last_login ON last_login.user_name = seen.username")
                .contains("WHERE (seen.username, seen.client_ip) NOT IN (SELECT user_name, login_ip FROM logins))");
    }

    @Test
    void should_not_alias_last_login_aggregates_to_the_columns_they_read() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql())
                .doesNotContain("max(login_time) AS login_time")
                .doesNotContain("argMax(first_name, login_time) AS first_name")
                .doesNotContain("argMax(last_name, login_time) AS last_name")
                .contains("last_login.last_first_name AS first_name")
                .contains("last_login.last_last_name AS last_name")
                .contains("last_login.last_login_time AS login_time");
    }

    @Test
    void should_not_expose_the_login_ip_as_its_own_column() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(pageSql())
                .doesNotContain("logins.login_ip AS login_ip")
                .contains("SELECT username, ip, first_name, last_name, toString(login_time) AS login_time_str,");
    }

    @Test
    void should_take_the_names_from_the_latest_user_protocol_row() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql())
                .contains("argMax(ifNull(first_name, ''), created_time) AS first_name")
                .contains("argMax(ifNull(last_name, ''), created_time) AS last_name");
    }

    @Test
    void should_expire_each_username_ip_pair_after_the_ttl() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql())
                .contains("max(pairs.last_action) AS last_activity")
                .contains("HAVING last_activity >= now() - 300");
    }

    @Test
    void should_end_every_pair_at_session_max_after_login() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql()).contains(" AND login_time >= now() - 900)");
    }

    @Test
    void should_honour_a_reconfigured_ttl() {
        properties.getActiveSessions().setTtl(Duration.ofMinutes(30));

        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql()).contains("HAVING last_activity >= now() - 1800");
        assertThat(pageSql()).contains(
                "toString(least(last_activity + INTERVAL 1800 SECOND, login_time + INTERVAL 900 SECOND)) AS expires_at");
    }

    @Test
    void should_cap_a_fifteen_minute_session_at_fifteen_minutes_after_login() {
        properties.getActiveSessions().setTtl(Duration.ofMinutes(15));
        repository = new ApiLogRepository(jdbc, properties);

        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql())
                .contains("WHERE event_time >= now() - 900")
                .contains("HAVING last_activity >= now() - 900 AND login_time >= now() - 900)");
        assertThat(pageSql()).contains(
                "toString(least(last_activity + INTERVAL 900 SECOND, login_time + INTERVAL 900 SECOND)) AS expires_at");
    }

    @Test
    void should_apply_the_internal_traffic_exclusions_to_activity() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql())
                .contains("AND parent_span_id = ''")
                .contains("AND username != 'anonymous'")
                .doesNotContain("isIPAddressInRange")
                .contains("AND client_ip != ''")
                .contains("AND NOT startsWith(endpoint, '/actuator')");
    }

    @Test
    void should_filter_by_username_when_provided() {
        var request = new ActiveSessionsRequest();
        request.setUsername("user@example.com");

        repository.activeSessions(request);

        assertThat(countSql()).contains(
                "WHERE created_time >= now() - 900 AND user_name = ? GROUP BY user_name, login_ip");
        assertThat(countArgs()).hasSize(1);
        assertThat(countArgs().get(0)).isEqualTo("user@example.com");
    }

    @Test
    void should_not_filter_by_username_when_blank() {
        var request = new ActiveSessionsRequest();
        request.setUsername("   ");

        repository.activeSessions(request);

        assertThat(countSql()).doesNotContain("user_name = ?");
        assertThat(countArgs()).isEmpty();
    }

    @Test
    void should_count_username_ip_pairs() {
        repository.activeSessions(new ActiveSessionsRequest());

        assertThat(countSql()).endsWith(" SELECT count() FROM sessions");
    }

    @Test
    void should_page_pairs_by_most_recent_activity() {
        var request = new ActiveSessionsRequest();
        request.setPageNumber(2);
        request.setPageSize(10);

        repository.activeSessions(request);

        assertThat(pageSql()).endsWith(" FROM sessions ORDER BY last_activity DESC, username, ip LIMIT ? OFFSET ?");
        assertThat(pageArgs()).containsExactly(10, 20L);
    }

    @Test
    void should_use_one_session_definition_for_count_and_page() {
        repository.activeSessions(new ActiveSessionsRequest());

        String count = countSql();
        String ctes = count.substring(0, count.lastIndexOf(" SELECT count() FROM sessions"));
        assertThat(ctes).startsWith("WITH logins AS (");
        assertThat(pageSql()).startsWith(ctes);
    }

    @Test
    void should_return_one_flat_row_per_ip_for_the_same_username() {
        var fromOffice = session("user@example.com", "127.0.0.1");
        var fromHome = session("user@example.com", "127.0.0.1");
        doReturn(List.of(fromOffice, fromHome)).when(jdbc).query(anyString(), any(RowMapper.class), any(Object[].class));

        var response = repository.activeSessions(new ActiveSessionsRequest());

        assertThat(response.getList()).containsExactly(fromOffice, fromHome);
        verify(jdbc).query(anyString(), any(RowMapper.class), any(Object[].class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_map_columns_by_their_aliases() throws Exception {
        repository.activeSessions(new ActiveSessionsRequest());

        ArgumentCaptor<RowMapper> captor = ArgumentCaptor.forClass(RowMapper.class);
        verify(jdbc).query(anyString(), captor.capture(), any(Object[].class));
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("username")).thenReturn("user@example.com");
        when(rs.getString("ip")).thenReturn("127.0.0.1");
        when(rs.getString("first_name")).thenReturn("John");
        when(rs.getString("last_name")).thenReturn("Doe");
        when(rs.getString("login_time_str")).thenReturn("2026-09-15 09:00:00.000");
        when(rs.getString("last_activity_str")).thenReturn("2026-09-15 10:05:00.000");
        when(rs.getString("expires_at")).thenReturn("2026-09-15 10:10:00.000");
        when(rs.getObject("requests")).thenReturn(12L);

        Object mapped = captor.getValue().mapRow(rs, 0);

        assertThat(mapped).isEqualTo(session("user@example.com", "127.0.0.1"));
    }

    @Test
    void should_report_paging_metadata_from_the_pair_total() {
        doReturn(7L).when(jdbc).queryForObject(anyString(), any(RowMapper.class), any(Object[].class));
        doReturn(List.of(session("user@example.com", "127.0.0.1")))
                .when(jdbc).query(anyString(), any(RowMapper.class), any(Object[].class));
        var request = new ActiveSessionsRequest();
        request.setPageSize(2);

        var response = repository.activeSessions(request);

        assertThat(response.getTotalElements()).isEqualTo(7L);
        assertThat(response.getTotalPages()).isEqualTo(4);
    }

    private static ActiveSession session(String username, String ip) {
        return new ActiveSession(username, ip, "John", "Doe", "2026-09-15 09:00:00.000",
                "2026-09-15 10:05:00.000", "2026-09-15 10:10:00.000", 12L);
    }

    private String countSql() {
        var captor = ArgumentCaptor.forClass(String.class);
        verify(jdbc).queryForObject(captor.capture(), any(RowMapper.class), any(Object[].class));
        return captor.getValue();
    }

    private List<Object> countArgs() {
        var captor = ArgumentCaptor.forClass(Object[].class);
        verify(jdbc).queryForObject(anyString(), any(RowMapper.class), captor.capture());
        return List.of(captor.getValue());
    }

    private String pageSql() {
        var captor = ArgumentCaptor.forClass(String.class);
        verify(jdbc).query(captor.capture(), any(RowMapper.class), any(Object[].class));
        return captor.getValue();
    }

    private List<Object> pageArgs() {
        var captor = ArgumentCaptor.forClass(Object[].class);
        verify(jdbc).query(anyString(), any(RowMapper.class), captor.capture());
        return List.of(captor.getValue());
    }
}
