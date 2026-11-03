package uz.nodir.notification.model.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uz.nodir.notification.model.enums.Provider;
import uz.repay.common.model.enums.Language;

@Data
@Configuration
@ConfigurationProperties(prefix = "internal.default", ignoreUnknownFields = false, ignoreInvalidFields = false)
public class DefaultProperty {
    private Language language;
    private Integer dailyLimit;
    private Provider provider;
}
