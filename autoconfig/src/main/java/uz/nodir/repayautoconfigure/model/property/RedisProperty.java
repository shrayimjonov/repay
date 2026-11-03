package uz.nodir.repayautoconfigure.model.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperty {
    private String host = "localhost";
    private Integer port = 6379;
    private String password;
}
