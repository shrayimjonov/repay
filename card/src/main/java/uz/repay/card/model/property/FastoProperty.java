package uz.repay.card.model.property;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "external.fasto", ignoreUnknownFields = false)
public class FastoProperty {

    private FastoProps sms;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FastoProps {
        private String url;
        private Integer readTimeOut;
        private Integer connectTimeOut;
    }

}
