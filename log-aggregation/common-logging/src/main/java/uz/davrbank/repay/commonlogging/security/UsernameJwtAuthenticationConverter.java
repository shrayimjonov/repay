package uz.davrbank.repay.commonlogging.security;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.util.StringUtils;

public final class UsernameJwtAuthenticationConverter {

    private UsernameJwtAuthenticationConverter() {
    }

    public static JwtAuthenticationConverter forClaim(String usernameClaim) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        if (StringUtils.hasText(usernameClaim)) {
            converter.setPrincipalClaimName(usernameClaim);
        }
        return converter;
    }
}
