package uz.repay.card.model.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(
        prefix = "external.nci-billing",
        ignoreInvalidFields = false,
        ignoreUnknownFields = false
)
public class NCIBillingProperty {
    private String url;
    private Integer readTimeOut;
    private Integer connectTimeOut;
}
