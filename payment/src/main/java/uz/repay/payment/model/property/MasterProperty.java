package uz.repay.payment.model.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.master-gate", ignoreInvalidFields = false, ignoreUnknownFields = false)
public class MasterProperty {
    private String url;
    private Integer readTimeOut;
    private Integer connectTimeOut;
    private String username;
    private String password;
}
