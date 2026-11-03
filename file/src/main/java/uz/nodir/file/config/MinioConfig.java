package uz.nodir.file.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.nodir.file.model.property.MiniOProperty;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final MiniOProperty property;

    @Bean
    public MinioClient bucketConfig() {
        return
                MinioClient.builder()
                        .endpoint(property.getUrl())
                        .credentials(property.getAccessKey(), property.getSecretKey())
                        .build();
    }
}
