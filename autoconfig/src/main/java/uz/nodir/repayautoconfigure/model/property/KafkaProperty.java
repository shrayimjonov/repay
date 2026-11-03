package uz.nodir.repayautoconfigure.model.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka", ignoreUnknownFields = false, ignoreInvalidFields = false)
public class KafkaProperty {

    private String bootstrapServers;

}
