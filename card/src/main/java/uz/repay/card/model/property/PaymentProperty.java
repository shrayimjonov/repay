package uz.repay.card.model.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.payment")
public class PaymentProperty {
    private String url;
    private Integer readTimeOut;
    private Integer connectTimeOut;
}
