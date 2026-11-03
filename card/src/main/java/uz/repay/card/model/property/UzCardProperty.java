package uz.repay.card.model.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "external.uzcard", ignoreUnknownFields = false)
public class UzCardProperty {
    private String integrationUrl;
    private Integer readTimeOut;
    private Integer connectTimeOut;
    private Integer responseTimeOut;
    private Integer writeTimeOut;
    private Integer maxInMemorySize;
}
