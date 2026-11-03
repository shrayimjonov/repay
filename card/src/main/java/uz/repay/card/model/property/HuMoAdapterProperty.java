package uz.repay.card.model.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uz.repay.card.model.enums.HuMoAdapter;
import uz.repay.card.model.enums.UzCardAdapter;

@Data
@Configuration
@ConfigurationProperties(prefix = "internal.humo", ignoreInvalidFields = false, ignoreUnknownFields = false)
public class HuMoAdapterProperty {
    private HuMoAdapter adapter;
}
