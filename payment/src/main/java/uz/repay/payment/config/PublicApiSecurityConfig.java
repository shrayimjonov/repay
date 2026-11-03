package uz.repay.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

/**
 * Security Configuration for Public APIs
 * This configuration allows public access to transaction history endpoint
 * by completely bypassing Spring Security for this path
 *
 */
@Configuration
public class PublicApiSecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/api/v1/transaction/history/**");
    }
}
