package uz.nodir.repayautoconfigure.model.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "schema-registry")
public class SchemaRegistryProperty {
    private String url;
}
