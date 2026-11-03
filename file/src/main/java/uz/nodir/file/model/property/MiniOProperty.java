package uz.nodir.file.model.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.minio")
public class MiniOProperty {
    private String url;
    private String accessKey;
    private String secretKey;
    private String bucket;
}
