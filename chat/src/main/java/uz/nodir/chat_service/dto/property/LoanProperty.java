package uz.nodir.chat_service.dto.property;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uz.repay.common.model.property.BaseProperty;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.loan")
@EqualsAndHashCode(callSuper = true)
public class LoanProperty extends BaseProperty {

}
