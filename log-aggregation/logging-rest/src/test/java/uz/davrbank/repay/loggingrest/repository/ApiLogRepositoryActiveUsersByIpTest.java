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
import uz.davrbank.repay.loggingrest.dto.ActiveUserIp;
import uz.davrbank.repay.loggingrest.dto.ActiveUsersIpRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApiLogRepositoryActiveUsersByIpTest {

    @Mock
    private JdbcTemplate jdbc;

    private ApiLogRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ApiLogRepository(jdbc, new LoggingProperties());
        doReturn(3L).when(jdbc).queryForObject(anyString(), any(RowMapper.class), any(Object[].class));
        doReturn(List.of()).when(jdbc).query(anyString(), any(RowMapper.class), any(Object[].class));
    }

    @Test
    void should_group_by_username_ip_and_mac_address() {
        repository.activeUsersByIp(request(null));

        assertThat(pageSql()).contains("GROUP BY username, client_ip, mac_address");
    }

    @Test
    void should_select_username_ip_mac_address_and_last_action() {
        repository.activeUsersByIp(request(null));

        assertThat(pageSql())
                .contains("SELECT username, client_ip, mac_address, toString(max(event_time)) AS last_action")
                .contains("FROM api_logs");
    }

    @Test
    void should_count_distinct_username_ip_and_mac_combinations() {
        repository.activeUsersByIp(request(null));

        assertThat(countSql()).contains("uniqExact(username, client_ip, mac_address)");
    }

    @Test
    void should_order_deterministically_for_stable_paging() {
        repository.activeUsersByIp(request(null));

        assertThat(pageSql()).contains("ORDER BY last_action DESC, username, client_ip, mac_address");
    }

    @Test
    void should_exclude_rows_without_username() {
        repository.activeUsersByIp(request(null));

        assertThat(pageSql()).contains("username != ''");
    }

    @Test
    void should_filter_by_username_when_provided() {
        repository.activeUsersByIp(request("user@example.com"));

        assertThat(pageSql()).contains("AND username = ?");
        assertThat(pageArgs()).contains("user@example.com");
    }

    @Test
    void should_not_filter_by_username_when_absent() {
        repository.activeUsersByIp(request(null));

        assertThat(pageSql()).doesNotContain("AND username = ?");
    }

    @Test
    void should_not_filter_by_username_when_blank() {
        repository.activeUsersByIp(request("   "));

        assertThat(pageSql()).doesNotContain("AND username = ?");
    }

    @Test
    void should_bind_username_before_paging_arguments() {
        var request = request("user@example.com");
        request.setPageNumber(2);
        request.setPageSize(20);

        repository.activeUsersByIp(request);

        assertThat(pageArgs()).containsExactly(
                request.getFrom().toString(), "user@example.com", 20, 40L);
    }

    @Test
    void should_bind_only_period_and_paging_when_username_absent() {
        var request = request(null);
        request.setPageNumber(1);
        request.setPageSize(50);

        repository.activeUsersByIp(request);

        assertThat(pageArgs()).containsExactly(request.getFrom().toString(), 50, 50L);
    }

    @Test
    void should_apply_the_same_filter_to_count_and_page_queries() {
        repository.activeUsersByIp(request("user@example.com"));

        assertThat(countSql()).contains("AND username = ?");
        assertThat(countArgs()).containsExactly(
                request("user@example.com").getFrom().toString(), "user@example.com");
    }

    @Test
    void should_default_to_the_last_day_when_period_absent() {
        var request = new ActiveUsersIpRequest();
        request.setPageSize(10);

        repository.activeUsersByIp(request);

        var bound = LocalDateTime.parse((String) pageArgs().get(0));

        assertThat(bound).isCloseTo(LocalDateTime.now().minusDays(1),
                within(5, java.time.temporal.ChronoUnit.SECONDS));
    }

    @Test
    void should_bind_period_as_parameter_not_inline_sql() {
        repository.activeUsersByIp(request("'; DROP TABLE api_logs; --"));

        assertThat(pageSql()).doesNotContain("DROP TABLE");
        assertThat(pageSql()).contains("parseDateTimeBestEffort(?)");
        assertThat(pageArgs()).contains("'; DROP TABLE api_logs; --");
    }

    @Test
    void should_map_row_to_username_ip_mac_address_and_last_action() throws SQLException {
        repository.activeUsersByIp(request(null));

        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("username")).thenReturn("user@example.com");
        when(rs.getString("client_ip")).thenReturn("127.0.0.1");
        when(rs.getString("mac_address")).thenReturn("A4:83:E7:2B:11:9C");
        when(rs.getString("last_action")).thenReturn("2026-09-10 10:40:25.265");

        ActiveUserIp row = pageMapper().mapRow(rs, 0);

        assertThat(row).isNotNull();
        assertThat(row.username()).isEqualTo("user@example.com");
        assertThat(row.ip()).isEqualTo("127.0.0.1");
        assertThat(row.macAddress()).isEqualTo("A4:83:E7:2B:11:9C");
        assertThat(row.lastActionDate()).isEqualTo("2026-09-10 10:40:25.265");
    }

    @Test
    void should_map_null_mac_address_when_header_was_absent() throws SQLException {
        repository.activeUsersByIp(request(null));

        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("username")).thenReturn("user@example.com");
        when(rs.getString("client_ip")).thenReturn("127.0.0.1");
        when(rs.getString("mac_address")).thenReturn(null);
        when(rs.getString("last_action")).thenReturn("2026-09-10 10:40:25.265");

        ActiveUserIp row = pageMapper().mapRow(rs, 0);

        assertThat(row).isNotNull();
        assertThat(row.macAddress()).isNull();
    }

    @Test
    void should_report_paging_metadata_from_total_pairs() {
        doReturn(7L).when(jdbc).queryForObject(anyString(), any(RowMapper.class), any(Object[].class));
        doReturn(List.of(
                new ActiveUserIp("a", "1.1.1.1", "AA:BB:CC:DD:EE:01", "2026-09-10 10:00:00"),
                new ActiveUserIp("a", "127.0.0.1", null, "2026-09-10 09:00:00")))
                .when(jdbc).query(anyString(), any(RowMapper.class), any(Object[].class));

        var request = request(null);
        request.setPageNumber(0);
        request.setPageSize(2);

        var response = repository.activeUsersByIp(request);

        assertThat(response.getList()).hasSize(2);
        assertThat(response.getTotalElements()).isEqualTo(7L);
        assertThat(response.getTotalPages()).isEqualTo(4);
    }

    private static org.assertj.core.data.TemporalUnitOffset within(long amount, java.time.temporal.ChronoUnit unit) {
        return new org.assertj.core.data.TemporalUnitWithinOffset(amount, unit);
    }

    private static ActiveUsersIpRequest request(String username) {
        var request = new ActiveUsersIpRequest();
        request.setFrom(LocalDateTime.of(2026, 9, 1, 0, 0, 0));
        request.setUsername(username);
        request.setPageSize(20);
        return request;
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

    @SuppressWarnings("unchecked")
    private RowMapper<ActiveUserIp> pageMapper() {
        var captor = ArgumentCaptor.forClass(RowMapper.class);
        verify(jdbc).query(anyString(), captor.capture(), any(Object[].class));
        return captor.getValue();
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
}
