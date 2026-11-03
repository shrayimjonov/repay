package uz.davrbank.repay.commonlogging.support;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ClientIpResolverTest {

    @Test
    void should_prefer_x_forwarded_for_over_x_real_ip() {
        var headers = headers(Map.of(
                "X-Forwarded-For", "127.0.0.1",
                "X-Real-IP", "127.0.0.1"));

        assertThat(ClientIpResolver.resolve(headers, "127.0.0.1")).isEqualTo("127.0.0.1");
    }

    @Test
    void should_take_the_original_client_from_a_forwarded_chain() {
        var headers = headers(Map.of("X-Forwarded-For", "127.0.0.1, 127.0.0.1, 127.0.0.1"));

        assertThat(ClientIpResolver.resolve(headers, "127.0.0.1")).isEqualTo("127.0.0.1");
    }

    @Test
    void should_fall_back_to_x_real_ip_when_forwarded_for_absent() {
        var headers = headers(Map.of("X-Real-IP", "127.0.0.1"));

        assertThat(ClientIpResolver.resolve(headers, "127.0.0.1")).isEqualTo("127.0.0.1");
    }

    @Test
    void should_fall_back_to_socket_address_when_no_headers_present() {
        assertThat(ClientIpResolver.resolve(headers(Map.of()), "127.0.0.1")).isEqualTo("127.0.0.1");
    }

    @Test
    void should_normalize_short_ipv6_loopback_to_ipv4() {
        assertThat(ClientIpResolver.resolve(headers(Map.of()), "::1")).isEqualTo("127.0.0.1");
    }

    @Test
    void should_normalize_expanded_ipv6_loopback_to_ipv4() {
        assertThat(ClientIpResolver.resolve(headers(Map.of()), "0:0:0:0:0:0:0:1")).isEqualTo("127.0.0.1");
    }

    @Test
    void should_normalize_fully_padded_ipv6_loopback_to_ipv4() {
        assertThat(ClientIpResolver.resolve(headers(Map.of()), "0000:0000:0000:0000:0000:0000:0000:0001"))
                .isEqualTo("127.0.0.1");
    }

    @Test
    void should_normalize_ipv6_loopback_arriving_in_a_header() {
        var headers = headers(Map.of("X-Forwarded-For", "::1"));

        assertThat(ClientIpResolver.resolve(headers, "127.0.0.1")).isEqualTo("127.0.0.1");
    }

    @Test
    void should_skip_unknown_header_values() {
        var headers = headers(Map.of(
                "X-Forwarded-For", "unknown",
                "X-Real-IP", "127.0.0.1"));

        assertThat(ClientIpResolver.resolve(headers, "127.0.0.1")).isEqualTo("127.0.0.1");
    }

    @Test
    void should_skip_unknown_entries_inside_a_forwarded_chain() {
        var headers = headers(Map.of("X-Forwarded-For", "unknown, 127.0.0.1"));

        assertThat(ClientIpResolver.resolve(headers, "127.0.0.1")).isEqualTo("127.0.0.1");
    }

    @Test
    void should_ignore_blank_headers() {
        var headers = headers(Map.of(
                "X-Forwarded-For", "   ",
                "X-Real-IP", "127.0.0.1"));

        assertThat(ClientIpResolver.resolve(headers, "127.0.0.1")).isEqualTo("127.0.0.1");
    }

    @Test
    void should_strip_port_from_ipv4_socket_address() {
        assertThat(ClientIpResolver.resolve(headers(Map.of()), "127.0.0.1:51514")).isEqualTo("127.0.0.1");
    }

    @Test
    void should_strip_brackets_and_port_from_ipv6_socket_address() {
        assertThat(ClientIpResolver.resolve(headers(Map.of()), "[::1]:8080")).isEqualTo("127.0.0.1");
    }

    @Test
    void should_keep_bare_ipv6_address_intact() {
        assertThat(ClientIpResolver.resolve(headers(Map.of()), "2001:db8::42")).isEqualTo("2001:db8::42");
    }

    @Test
    void should_fall_back_to_lower_priority_proxy_headers() {
        var headers = headers(Map.of("CF-Connecting-IP", "127.0.0.1"));

        assertThat(ClientIpResolver.resolve(headers, "127.0.0.1")).isEqualTo("127.0.0.1");
    }

    @Test
    void should_report_loopback_for_normalized_ipv6_loopback() {
        assertThat(ClientIpResolver.isLoopback("::1")).isTrue();
        assertThat(ClientIpResolver.isLoopback("127.0.0.1")).isTrue();
        assertThat(ClientIpResolver.isLoopback("127.0.0.1")).isFalse();
    }

    private static java.util.function.UnaryOperator<String> headers(Map<String, String> values) {
        return values::get;
    }
}
