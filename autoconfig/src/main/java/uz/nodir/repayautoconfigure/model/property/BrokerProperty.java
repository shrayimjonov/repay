package uz.nodir.repayautoconfigure.model.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uz.nodir.repayautoconfigure.model.enums.BrokerType;


@Data
@Configuration
@ConfigurationProperties(prefix = "broker", ignoreInvalidFields = false, ignoreUnknownFields = false)
public class BrokerProperty {
    private BrokerType type;
}
