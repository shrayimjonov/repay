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
@ConfigurationProperties(prefix = "external.humo-gateway")
public class HumoGatewayProperty {
    private String url;
    private Integer readTimeOut;
    private Integer connectTimeOut;
    private String username;
    private String password;
    private String originator;
}
