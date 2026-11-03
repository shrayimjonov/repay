package uz.repay.loan.model.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uz.repay.common.model.property.BaseProperty;


@Data
@EqualsAndHashCode(callSuper=false)
@Configuration
@ConfigurationProperties(prefix = "external.gateway")
public class GatewayAuthProperty extends BaseProperty {
    private String username;
    private String password;
    private GatewayBasicAuthCredentials basicAuthCredentials;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GatewayBasicAuthCredentials{
        private String username;
        private String password;
    }
}
