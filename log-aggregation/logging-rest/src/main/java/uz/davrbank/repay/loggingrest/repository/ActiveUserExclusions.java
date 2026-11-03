package uz.davrbank.repay.loggingrest.repository;

import uz.davrbank.repay.commonlogging.config.LoggingProperties;

import java.util.*;
import java.util.regex.Pattern;

public final class ActiveUserExclusions {

    private static final Pattern ENDPOINT = Pattern.compile("^[A-Za-z0-9/_.{}*\\-]{1,160}$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9._@\\-]{1,160}$");

    private final LoggingProperties.ActiveSessions config;

    public ActiveUserExclusions(LoggingProperties properties) {
        this.config = properties.getActiveSessions();
    }

    public String sql() {
        StringBuilder sb = new StringBuilder();
        if (config.isExcludeInternalHops()) {
            sb.append(" AND parent_span_id = ''");
        }
        for (String username : config.getExcludedUsernames()) {
            sb.append(" AND username != ").append(literal(username, USERNAME, "username"));
        }
        for (String endpoint : config.getExcludedEndpoints()) {
            sb.append(" AND NOT startsWith(endpoint, ")
                    .append(literal(endpoint, ENDPOINT, "endpoint"))
                    .append(")");
        }
        sb.append(" AND client_ip != ''");
        return sb.toString();
    }

    private static String literal(String value, Pattern allowed, String what) {
        if (value == null || !allowed.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Unsupported " + what + " in repay.logging.active-sessions config: " + value);
        }
        return "'" + value + "'";
    }
}
