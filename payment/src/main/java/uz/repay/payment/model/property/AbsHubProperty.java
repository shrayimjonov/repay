package uz.repay.payment.model.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.abs-hub")
public class AbsHubProperty {
    private String username;
    private String password;
    private String url;
    private Integer readTimeOut;
    private Integer connectTimeOut;
}
