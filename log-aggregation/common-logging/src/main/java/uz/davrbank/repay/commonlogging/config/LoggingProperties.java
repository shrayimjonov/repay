package uz.davrbank.repay.commonlogging.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.*;

@Setter
@Getter
@ConfigurationProperties(prefix = "repay.logging")
public class LoggingProperties {

    private boolean enabled = true;

    private boolean feignEnabled = true;

    private String service = "";

    private boolean captureBodies = true;

    private int maxBodyBytes = 16 * 1024;

    private String zone = "Asia/Tashkent";

    private String macHeader = "X-Device-Mac";

    private String usernameClaim = "preferred_username";

    private ActiveSessions activeSessions = new ActiveSessions();

    @Setter
    @Getter
    public static class ActiveSessions {

        private Duration ttl = Duration.ofMinutes(15);

        private List<String> excludedEndpoints = new ArrayList<>(List.of(
                "/actuator",
                "/health",
                "/error",
                "/favicon.ico",
                "/v3/api-docs",
                "/swagger-ui",
                "/webjars",
                "/swagger-resources"
        ));

        private List<String> excludedUsernames = new ArrayList<>(List.of(
                "anonymous"
        ));

        private boolean excludeInternalHops = true;
    }
}
