package uz.davrbank.repay.commonlogging.support;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.UnaryOperator;

public final class ClientIpResolver {

    public static final String LOOPBACK_IPV4 = "127.0.0.1";

    private static final List<String> FORWARDED_HEADERS = List.of(
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "CF-Connecting-IP"
    );

    private static final Set<String> IPV6_LOOPBACKS = Set.of(
            "::1",
            "0:0:0:0:0:0:0:1",
            "0000:0000:0000:0000:0000:0000:0000:0001"
    );

    private static final String UNKNOWN = "unknown";

    private ClientIpResolver() {
    }

    public static String resolve(HttpServletRequest request) {
        return resolve(request::getHeader, request.getRemoteAddr());
    }

    public static String resolve(UnaryOperator<String> headerLookup, String remoteAddr) {
        for (String header : FORWARDED_HEADERS) {
            String candidate = firstUsable(headerLookup.apply(header));
            if (candidate != null) {
                return candidate;
            }
        }
        String direct = normalize(remoteAddr);
        return direct != null ? direct : remoteAddr;
    }

    public static boolean isLoopback(String ip) {
        String normalized = normalize(ip);
        return normalized != null && normalized.startsWith("127.");
    }

    private static String firstUsable(String headerValue) {
        if (!StringUtils.hasText(headerValue)) {
            return null;
        }
        for (String part : headerValue.split(",")) {
            String ip = normalize(part);
            if (ip != null) {
                return ip;
            }
        }
        return null;
    }

    private static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        String ip = stripPort(raw.trim());
        if (ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            return null;
        }
        if (IPV6_LOOPBACKS.contains(ip.toLowerCase(Locale.ROOT))) {
            return LOOPBACK_IPV4;
        }
        return ip;
    }

    private static String stripPort(String value) {
        if (value.startsWith("[")) {
            int close = value.indexOf(']');
            return close > 0 ? value.substring(1, close) : value;
        }
        int firstColon = value.indexOf(':');
        if (firstColon > 0 && firstColon == value.lastIndexOf(':')) {
            return value.substring(0, firstColon);
        }
        return value;
    }
}
