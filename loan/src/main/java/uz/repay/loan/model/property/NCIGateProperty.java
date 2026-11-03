package uz.repay.loan.model.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "external.nci-billing", ignoreUnknownFields = false)
public class NCIGateProperty {
    private String url;
    private Integer readTimeOut;
    private Integer connectTimeOut;
}
