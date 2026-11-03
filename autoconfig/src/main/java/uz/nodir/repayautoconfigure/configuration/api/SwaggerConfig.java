package uz.nodir.repayautoconfigure.configuration.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition(info = @Info(title = "repay-service", version = "1.0.1"), security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(type = SecuritySchemeType.HTTP, bearerFormat = "jwt", name = "bearerAuth", scheme = "bearer", in = SecuritySchemeIn.HEADER)
@Slf4j
@ConditionalOnClass(OpenAPI.class)
public class SwaggerConfig {


    @PostConstruct
    public void init() {
        log.info("Swagger Open-API Configuration is enabled!");
    }

}

