package uz.repay.loan.model.property;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uz.repay.common.model.property.BaseProperty;


@Data
@Configuration
@ConfigurationProperties(prefix = "external.client", ignoreUnknownFields = false, ignoreInvalidFields = false)
@EqualsAndHashCode(callSuper = true)
public class ClientServiceProperty extends BaseProperty {

}
