package uz.davrbank.repay.loggingrest.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearer-jwt";

    @Value("${openapi.server-url:/loan-api/loggingrest}")
    private String serverUrl;

    @Bean
    public OpenAPI loggingRestOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("logging-rest API")
                        .version("v1")
                        .description("Logging and Auditing api's"))
                .servers(List.of(new Server()
                        .url(serverUrl)
                        .description("API gateway")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
