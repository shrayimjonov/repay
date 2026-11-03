package uz.repay.card.model.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.humo-gateway", ignoreUnknownFields = false, ignoreInvalidFields = false)
public class HuMoGatewayProperty {
    private String url;
    private Integer readTimeOut;
    private Integer connectTimeOut;
}
