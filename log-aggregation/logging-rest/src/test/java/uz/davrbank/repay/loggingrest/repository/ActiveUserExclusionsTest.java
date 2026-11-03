package uz.davrbank.repay.loggingrest.repository;

import org.junit.jupiter.api.Test;
import uz.davrbank.repay.commonlogging.config.LoggingProperties;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActiveUserExclusionsTest {

    @Test
    void should_exclude_internal_hops_by_parent_span() {
        assertThat(sqlOf(new LoggingProperties())).contains("AND parent_span_id = ''");
    }

    @Test
    void should_not_exclude_internal_hops_when_disabled() {
        var properties = new LoggingProperties();
        properties.getActiveSessions().setExcludeInternalHops(false);

        assertThat(sqlOf(properties)).doesNotContain("parent_span_id");
    }

    @Test
    void should_exclude_anonymous_traffic_by_default() {
        assertThat(sqlOf(new LoggingProperties())).contains("AND username != 'anonymous'");
    }

    @Test
    void should_never_filter_intranet_users_by_ip_range() {
        String sql = sqlOf(new LoggingProperties());

        assertThat(sql)
                .doesNotContain("isIPAddressInRange")
                .doesNotContain("127.0.0.0/8")
                .doesNotContain("127.0.0.1/8")
                .doesNotContain("127.0.0.1/12")
                .doesNotContain("127.0.0.1/16");
    }

    @Test
    void should_require_a_non_empty_client_ip() {
        assertThat(sqlOf(new LoggingProperties())).contains("AND client_ip != ''");
    }

    @Test
    void should_exclude_actuator_and_health_endpoints_by_default() {
        String sql = sqlOf(new LoggingProperties());

        assertThat(sql)
                .contains("AND NOT startsWith(endpoint, '/actuator')")
                .contains("AND NOT startsWith(endpoint, '/health')");
    }

    @Test
    void should_keep_only_the_client_ip_guard_when_every_exclusion_is_cleared() {
        var properties = new LoggingProperties();
        var sessions = properties.getActiveSessions();
        sessions.setExcludeInternalHops(false);
        sessions.setExcludedEndpoints(new ArrayList<>());
        sessions.setExcludedUsernames(new ArrayList<>());

        assertThat(sqlOf(properties)).isEqualTo(" AND client_ip != ''");
    }

    @Test
    void should_reject_an_endpoint_carrying_a_quote() {
        var properties = new LoggingProperties();
        properties.getActiveSessions().setExcludedEndpoints(
                new ArrayList<>(List.of("/actuator') OR 1=1 --")));

        assertThatThrownBy(() -> sqlOf(properties))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("endpoint");
    }

    @Test
    void should_reject_a_username_carrying_a_quote() {
        var properties = new LoggingProperties();
        properties.getActiveSessions().setExcludedUsernames(
                new ArrayList<>(List.of("anon' OR '1'='1")));

        assertThatThrownBy(() -> sqlOf(properties))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username");
    }

    private static String sqlOf(LoggingProperties properties) {
        return new ActiveUserExclusions(properties).sql();
    }
}
