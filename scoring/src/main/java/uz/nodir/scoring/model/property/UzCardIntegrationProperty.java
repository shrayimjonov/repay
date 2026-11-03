package uz.nodir.scoring.model.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uz.repay.common.model.property.BaseProperty;


@Data
@Configuration
@ConfigurationProperties(prefix = "external.uzcard-integration", ignoreUnknownFields = false, ignoreInvalidFields = false)
public class UzCardIntegrationProperty extends BaseProperty {
}
