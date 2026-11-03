package uz.repay.payment.model.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uz.repay.common.model.enums.Branch;

import java.util.Map;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "external.billing")
public class BillingProperty {
    private String url;
    private Integer readTimeOut;
    private Integer connectTimeOut;
    private Keycloak keycloak;
    private Map<Branch, String> services;
    private Map<Branch, String> dummy;

    @Getter
    @Setter
    @ToString
    public static class Keycloak {
        private String url;
        private String username;
        private String password;
        private String clientId;
        private String clientSecret;
        private String grantType;
        private String scope;
    }

}
