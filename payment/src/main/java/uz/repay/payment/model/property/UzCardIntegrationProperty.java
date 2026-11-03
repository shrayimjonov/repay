package uz.repay.payment.model.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "external.uzcard-integration")
public class UzCardIntegrationProperty {
    private String url;
    private Integer readTimeOut;
    private Integer connectTimeOut;
}
