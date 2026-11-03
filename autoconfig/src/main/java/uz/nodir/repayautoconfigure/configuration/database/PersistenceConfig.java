package uz.nodir.repayautoconfigure.configuration.database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uz.nodir.repayautoconfigure.service.AuditorAwareService;

import java.util.Optional;


@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@Configuration
public class PersistenceConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            var sc = SecurityContextHolder.getContext();

            if (sc == null || sc.getAuthentication() == null)
                return Optional.of("system");
            else if (sc.getAuthentication() instanceof AnonymousAuthenticationToken || ((JwtAuthenticationToken) sc.getAuthentication()).getToken() == null)
                return Optional.of("external");
            else
                return new AuditorAwareService().getCurrentAuditor();
        };
    }
}
