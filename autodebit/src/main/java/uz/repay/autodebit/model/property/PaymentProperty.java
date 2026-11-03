package uz.repay.autodebit.model.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "external.payment")
public class PaymentProperty {
    private String url;
    private Integer readTimeOut;
    private Integer connectTimeOut;
}
