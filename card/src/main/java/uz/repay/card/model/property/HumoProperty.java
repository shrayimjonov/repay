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
@ConfigurationProperties(prefix = "external.humo", ignoreUnknownFields = false)
public class HumoProperty {
    private String integrationUrl;
    private Integer readTimeOut;
    private Integer connectTimeOut;
    private String url;
    private String token;

}
