package uz.nodir.repayautoconfigure.service;


import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.common.model.dto.response.keycloak.UserRoleDTO;


import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();


    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                getPrincipleClaimName(jwt)
        );
    }

    private String getPrincipleClaimName(Jwt jwt) {
        return jwt.getClaim(SecurityUtils.principalAttribute);
    }

    public Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        var userRole = new UserRoleDTO(jwt.getClaims());

        Collection<String> resourceRoles = userRole.realmAccess().roles();

        return resourceRoles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    public Collection<? extends GrantedAuthority> extractBranches(Jwt jwt) {
        var userRole = new UserRoleDTO(jwt.getClaims());

        Collection<String> resourceRoles = userRole.clientBranch();

        return resourceRoles
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}