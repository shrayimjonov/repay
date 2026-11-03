package uz.repay.loan.model.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.nci-gateway")
public class NCIGatewayProperty {
    private String url;
    private Integer readTimeOut;
    private Integer connectTimeOut;
}
