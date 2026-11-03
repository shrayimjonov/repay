package uz.repay.card.model.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.visa-middleware", ignoreUnknownFields = false, ignoreInvalidFields = false)
public class VisaProperty {
    private String url;
    private Integer readTimeOut;
    private Integer connectTimeOut;
}
