package uz.davrbank.repay.loggingrest.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import uz.davrbank.repay.commonlogging.config.LoggingProperties;
import uz.davrbank.repay.loggingrest.dto.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class ApiLogRepository {

    private static final String NODE_COLS = """
            toString(event_time) AS event_time_str, trace_id, span_id, parent_span_id,
            toString(direction) AS direction, service, username, client_ip, endpoint, http_method,
            status_code, success, duration_ms, attempt
            """;

    private final JdbcTemplate jdbc;
    private final LoggingProperties.ActiveSessions sessionConfig;
    private final ActiveUserExclusions exclusions;
    private final Duration sessionMax;

    public ApiLogRepository(JdbcTemplate jdbc,
                            LoggingProperties properties) {
        this.jdbc = jdbc;
        this.sessionConfig = properties.getActiveSessions();
        this.exclusions = new ActiveUserExclusions(properties);
        this.sessionMax = Duration.ofMinutes(15);
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private static Integer asInt(java.sql.ResultSet rs, String col) throws java.sql.SQLException {
        Object o = rs.getObject(col);
        return o == null ? null : ((Number) o).intValue();
    }

    private static Long asLong(java.sql.ResultSet rs, String col) throws java.sql.SQLException {
        Object o = rs.getObject(col);
        return o == null ? null : ((Number) o).longValue();
    }

    private static RowMapper<LogNode> nodeMapper(Set<String> spansWithChildren) {
        return (rs, i) -> {
            String spanId = rs.getString("span_id");
            return new LogNode(
                    rs.getString("event_time_str"), rs.getString("trace_id"), spanId,
                    rs.getString("parent_span_id"), rs.getString("direction"),
                    rs.getString("service"), rs.getString("username"), rs.getString("client_ip"),
                    rs.getString("endpoint"), rs.getString("http_method"),
                    asInt(rs, "status_code"), rs.getBoolean("success"),
                    asLong(rs, "duration_ms"), asInt(rs, "attempt"),
                    spansWithChildren.contains(spanId));
        };
    }

    public ApiLogPageResponseDTO roots(ApiLogFilter filter) {
        long secs = (long) filter.getSinceMinutes() * 60;

        StringBuilder where = new StringBuilder(
                " FROM api_logs WHERE parent_span_id = '' AND event_time >= now() - " + secs);
        List<Object> args = new ArrayList<>();
        if (notBlank(filter.getService())) {
            where.append(" AND service = ?");
            args.add(filter.getService());
        }
        if (filter.getSuccess() != null) {
            where.append(" AND success = ?");
            args.add(filter.getSuccess());
        }
        if (notBlank(filter.getUsername())) {
            where.append(" AND username = ?");
            args.add(filter.getUsername());
        }
        if (notBlank(filter.getClientIp())) {
            where.append(" AND client_ip = ?");
            args.add(filter.getClientIp());
        }

        Long total = jdbc.queryForObject("SELECT count()" + where, (rs, i) -> rs.getLong(1), args.toArray());
        long totalElements = total != null ? total : 0L;

        int pageNumber = filter.getPageNumber();
        int pageSize = filter.getPageSize();

        List<Object> pageArgs = new ArrayList<>(args);
        pageArgs.add(pageSize);
        pageArgs.add((long) pageNumber * pageSize);

        Set<String> withChildren = spanIdsThatAreParentsInWindow(secs);
        String sql = "SELECT " + NODE_COLS + where + " ORDER BY event_time DESC LIMIT ? OFFSET ?";
        List<LogNode> content = jdbc.query(sql, nodeMapper(withChildren), pageArgs.toArray());

        return new ApiLogPageResponseDTO(content, pageNumber, pageSize, totalElements);
    }

    public List<LogNode> children(String parentSpanId) {
        String sql = "SELECT " + NODE_COLS
                + " FROM api_logs WHERE parent_span_id = ? ORDER BY event_time";
        List<LogNode> rows = jdbc.query(sql, nodeMapper(Set.of()), parentSpanId);
        if (rows.isEmpty()) {
            return rows;
        }
        List<String> spanIds = rows.stream()
                .map(LogNode::spanId)
                .filter(s -> s != null && !s.isBlank())
                .toList();
        Set<String> withChildren = spanIds.isEmpty() ? Set.of() : parentsAmong(spanIds);
        return rows.stream()
                .map(n -> new LogNode(
                        n.eventTime(), n.traceId(), n.spanId(), n.parentSpanId(), n.direction(),
                        n.service(), n.username(), n.clientIp(), n.endpoint(), n.httpMethod(), n.statusCode(), n.success(),
                        n.durationMs(), n.attempt(), withChildren.contains(n.spanId())))
                .toList();
    }

    public List<ApiLogRow> traceTree(String traceId) {
        String sql = """
                SELECT toString(event_time) AS event_time_str, trace_id, span_id, parent_span_id,
                       toString(direction) AS direction, service, username, toString(client_ip) AS client_ip,
                       endpoint, http_method, status_code, success, duration_ms, attempt, error_message,
                       request_body, response_body
                FROM api_logs WHERE trace_id = ? ORDER BY event_time
                """;
        return jdbc.query(sql, (rs, i) -> new ApiLogRow(
                rs.getString("event_time_str"), rs.getString("trace_id"), rs.getString("span_id"),
                rs.getString("parent_span_id"), rs.getString("direction"), rs.getString("service"),
                rs.getString("username"), rs.getString("client_ip"), rs.getString("endpoint"),
                rs.getString("http_method"), asInt(rs, "status_code"),
                rs.getBoolean("success"), asLong(rs, "duration_ms"),
                asInt(rs, "attempt"), rs.getString("error_message"),
                rs.getString("request_body"), rs.getString("response_body")), traceId);
    }

    public ActiveUserPageResponseDTO activeUsers(ActiveUsersRequest request) {
        LocalDateTime fromDt = request.getFrom() != null ? request.getFrom() : LocalDateTime.now().minusDays(1);
        String from = fromDt.toString();

        String where = " FROM api_logs WHERE event_time >= parseDateTimeBestEffort(?) AND username != ''"
                + exclusions.sql();

        Long total = jdbc.queryForObject(
                "SELECT uniqExact(username)" + where,
                (rs, i) -> rs.getLong(1), from);
        long totalElements = total != null ? total : 0L;

        int pageNumber = request.getPageNumber();
        int pageSize = request.getPageSize();

        String sql = "SELECT username, toString(max(event_time)) AS last_action" + where
                + " GROUP BY username ORDER BY last_action DESC LIMIT ? OFFSET ?";
        List<ActiveUser> content = jdbc.query(sql, (rs, i) -> new ActiveUser(
                        rs.getString("username"), rs.getString("last_action")),
                from, pageSize, (long) pageNumber * pageSize);

        return new ActiveUserPageResponseDTO(content, pageNumber, pageSize, totalElements);
    }

    public ActiveUserIpPageResponseDTO activeUsersByIp(ActiveUsersIpRequest request) {
        LocalDateTime fromDt = request.getFrom() != null ? request.getFrom() : LocalDateTime.now().minusDays(1);

        StringBuilder where = new StringBuilder(
                " FROM api_logs WHERE event_time >= parseDateTimeBestEffort(?) AND username != ''")
                .append(exclusions.sql());
        List<Object> args = new ArrayList<>();
        args.add(fromDt.toString());

        if (notBlank(request.getUsername())) {
            where.append(" AND username = ?");
            args.add(request.getUsername());
        }

        Long total = jdbc.queryForObject(
                "SELECT uniqExact(username, client_ip, mac_address)" + where,
                (rs, i) -> rs.getLong(1), args.toArray());
        long totalElements = total != null ? total : 0L;

        int pageNumber = request.getPageNumber();
        int pageSize = request.getPageSize();

        List<Object> pageArgs = new ArrayList<>(args);
        pageArgs.add(pageSize);
        pageArgs.add((long) pageNumber * pageSize);

        String sql = "SELECT username, client_ip, mac_address, toString(max(event_time)) AS last_action"
                + where
                + " GROUP BY username, client_ip, mac_address"
                + " ORDER BY last_action DESC, username, client_ip, mac_address LIMIT ? OFFSET ?";

        List<ActiveUserIp> content = jdbc.query(sql, (rs, i) -> new ActiveUserIp(
                        rs.getString("username"), rs.getString("client_ip"),
                        rs.getString("mac_address"), rs.getString("last_action")),
                pageArgs.toArray());

        return new ActiveUserIpPageResponseDTO(content, pageNumber, pageSize, totalElements);
    }

    public ActiveSessionPageResponseDTO activeSessions(ActiveSessionsRequest request) {
        long ttlSeconds = Math.max(1L, sessionConfig.getTtl().getSeconds());
        long sessionMaxSeconds = Math.max(1L, sessionMax.getSeconds());
        long lookbackSeconds = Math.max(ttlSeconds, sessionMaxSeconds);

        List<Object> args = new ArrayList<>();
        String ctes = sessionCtes(request, ttlSeconds, sessionMaxSeconds, lookbackSeconds, args);

        Long total = jdbc.queryForObject(
                ctes + " SELECT count() FROM sessions",
                (rs, i) -> rs.getLong(1), args.toArray());
        long totalElements = total != null ? total : 0L;

        int pageNumber = request.getPageNumber();
        int pageSize = request.getPageSize();

        List<Object> pageArgs = new ArrayList<>(args);
        pageArgs.add(pageSize);
        pageArgs.add((long) pageNumber * pageSize);

        String sql = ctes
                + " SELECT username, ip, first_name, last_name, toString(login_time) AS login_time_str,"
                + " toString(last_activity) AS last_activity_str,"
                + " toString(least(last_activity + INTERVAL " + ttlSeconds + " SECOND,"
                + " login_time + INTERVAL " + sessionMaxSeconds + " SECOND)) AS expires_at, requests"
                + " FROM sessions ORDER BY last_activity DESC, username, ip LIMIT ? OFFSET ?";
        List<ActiveSession> content = jdbc.query(sql, (rs, i) -> new ActiveSession(
                rs.getString("username"), rs.getString("ip"),
                rs.getString("first_name"), rs.getString("last_name"),
                rs.getString("login_time_str"),
                rs.getString("last_activity_str"), rs.getString("expires_at"),
                asLong(rs, "requests")), pageArgs.toArray());

        return new ActiveSessionPageResponseDTO(content, pageNumber, pageSize, totalElements);
    }

    private String sessionCtes(ActiveSessionsRequest request, long ttlSeconds, long sessionMaxSeconds,
                               long lookbackSeconds, List<Object> args) {
        StringBuilder logins = new StringBuilder(
                "SELECT user_name, ifNull(ip_address, '') AS login_ip,"
                        + " argMax(ifNull(first_name, ''), created_time) AS first_name,"
                        + " argMax(ifNull(last_name, ''), created_time) AS last_name,"
                        + " max(created_time) AS login_time"
                        + " FROM keycloak_user_protocol"
                        + " WHERE created_time >= now() - " + lookbackSeconds);
        if (notBlank(request.getUsername())) {
            logins.append(" AND user_name = ?");
            args.add(request.getUsername());
        }
        logins.append(" GROUP BY user_name, login_ip");

        return "WITH logins AS (" + logins + ")"
                + ", last_login AS (SELECT user_name,"
                + " argMax(first_name, login_time) AS last_first_name,"
                + " argMax(last_name, login_time) AS last_last_name,"
                + " max(login_time) AS last_login_time FROM logins GROUP BY user_name)"
                + ", pair_login AS (SELECT user_name, login_ip, first_name, last_name, login_time FROM logins"
                + " UNION ALL SELECT seen.username AS user_name, seen.client_ip AS login_ip,"
                + " last_login.last_first_name AS first_name, last_login.last_last_name AS last_name,"
                + " last_login.last_login_time AS login_time"
                + " FROM (SELECT DISTINCT username, client_ip FROM api_logs"
                + " WHERE event_time >= now() - " + lookbackSeconds
                + " AND success AND username != ''" + exclusions.sql() + ") AS seen"
                + " INNER JOIN last_login ON last_login.user_name = seen.username"
                + " WHERE (seen.username, seen.client_ip) NOT IN (SELECT user_name, login_ip FROM logins))"
                + ", ip_activity AS (SELECT username, client_ip, max(event_time) AS last_action,"
                + " count() AS request_count"
                + " FROM api_logs INNER JOIN pair_login ON pair_login.user_name = api_logs.username"
                + " AND pair_login.login_ip = api_logs.client_ip"
                + " WHERE event_time >= now() - " + lookbackSeconds
                + " AND event_time >= login_time AND success AND username != ''"
                + exclusions.sql()
                + " GROUP BY username, client_ip)"
                + ", pairs AS (SELECT username, client_ip, last_action, request_count FROM ip_activity"
                + " UNION ALL SELECT user_name AS username, login_ip AS client_ip,"
                + " login_time AS last_action, toUInt64(0) AS request_count FROM pair_login)"
                + ", sessions AS (SELECT pairs.username AS username, pairs.client_ip AS ip,"
                + " pair_login.first_name AS first_name, pair_login.last_name AS last_name,"
                + " pair_login.login_time AS login_time,"
                + " max(pairs.last_action) AS last_activity, sum(pairs.request_count) AS requests"
                + " FROM pairs INNER JOIN pair_login ON pair_login.user_name = pairs.username"
                + " AND pair_login.login_ip = pairs.client_ip"
                + " GROUP BY pairs.username, pairs.client_ip, pair_login.first_name, pair_login.last_name,"
                + " pair_login.login_time"
                + " HAVING last_activity >= now() - " + ttlSeconds
                + " AND login_time >= now() - " + sessionMaxSeconds + ")";
    }

    public StatisticsResponse statistics(LocalDate date) {
        String sql = """
                SELECT count()               AS total,
                       countIf(success)      AS success_cnt,
                       countIf(NOT success)  AS error_cnt
                FROM api_logs
                WHERE toDate(event_time) = toDate(?) AND direction = 'IN'
                """;
        return jdbc.queryForObject(sql, (rs, i) -> new StatisticsResponse(
                        date, rs.getLong("total"), rs.getLong("success_cnt"), rs.getLong("error_cnt")),
                date.toString());
    }

    private Set<String> spanIdsThatAreParentsInWindow(long secs) {
        return new HashSet<>(jdbc.queryForList(
                "SELECT DISTINCT parent_span_id FROM api_logs "
                        + "WHERE event_time >= now() - " + secs + " AND parent_span_id != ''",
                String.class));
    }

    private Set<String> parentsAmong(List<String> spanIds) {
        String placeholders = String.join(",", java.util.Collections.nCopies(spanIds.size(), "?"));
        return new HashSet<>(jdbc.queryForList(
                "SELECT DISTINCT parent_span_id FROM api_logs WHERE parent_span_id IN (" + placeholders + ")",
                String.class, spanIds.toArray()));
    }
}
