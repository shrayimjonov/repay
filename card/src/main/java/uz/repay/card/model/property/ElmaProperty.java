package uz.repay.card.model.property;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uz.repay.common.model.property.BaseProperty;


@Data
@Configuration
@ConfigurationProperties(prefix = "external.elma", ignoreUnknownFields = false, ignoreInvalidFields = false)
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class ElmaProperty extends BaseProperty {
    private String username;
    private String password;
}
