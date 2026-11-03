package uz.repay.card.model.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "external.svgate", ignoreUnknownFields = false, ignoreInvalidFields = false)
public class SVGateProperty {
    private String url;
    private Integer readTimeOut;
    private Integer connectTimeOut;
    private String username;
    private String password;
}
