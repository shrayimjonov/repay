package uz.repay.card.model.property;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uz.repay.common.model.property.BaseProperty;


@Data
@Configuration
@ConfigurationProperties(prefix = "external.humo-mware", ignoreUnknownFields = false, ignoreInvalidFields = false)
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class HuMoMiddlewareProperty extends BaseProperty {
    private String token;
}
