package uz.nodir.repayautoconfigure.configuration.security;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import uz.nodir.repayautoconfigure.service.JwtAuthConverter;
import uz.repay.common.model.dto.response.core.ResultData;


import java.io.IOException;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthConverter jwtAuthConverter;


    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> {
                            auth.requestMatchers(
                                            "/api/v1/internal/**",
                                            "/api/v2/internal/**",
                                            "/api/v3/internal/**",
                                            "/swagger-ui/**", "/swagger/**", "/v3/api-docs/**",
                                            "/actuator/**",
                                    "/static/**",
                                    "/images/**",
                                    "/api/v1/auto-pay/**",
                                    "/api/v1/card_event/**"
                                    )
                                    .permitAll();
                            auth.anyRequest()
                                    .authenticated();
                        }
                )
                .oauth2ResourceServer(
                        res -> {
                            res.accessDeniedHandler((request, response, accessDeniedException) -> handleAccessDenied(response));

                            res.jwt(
                                    jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthConverter)
                            );
                        }
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS));


        return http.build();
    }

    private static void handleAccessDenied(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.OK.value());
        var responseData = new ResultData<>(401, "You don't have any permission");
        response.getWriter().write(
                responseData.errorToJson()
        );
    }
}
