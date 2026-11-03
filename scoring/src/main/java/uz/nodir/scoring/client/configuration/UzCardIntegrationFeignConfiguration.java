package uz.nodir.scoring.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import uz.nodir.scoring.model.property.UzCardIntegrationProperty;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class UzCardIntegrationFeignConfiguration {
    private final UzCardIntegrationProperty property;

    @Bean
    public ErrorDecoder errorDecoderUzCardIntegration() {
        ErrorDecoder.Default eDecoder = new ErrorDecoder.Default();
        log.info("Prepared error decoder for uzCard integration feign client: {}", eDecoder);
        return eDecoder;
    }

    @Bean
    public Logger.Level feignLoggerLevelUzCardIntegration() {
        Logger.Level level = Logger.Level.FULL;
        log.info("Prepared logger level for uzCard integration feign client: {}", level);
        return level;
    }


    @Bean
    public Request.Options getOptionsUzCardIntegration() {
        Request.Options rOptions = new Request.Options(
                property.getConnectTimeOut(),
                TimeUnit.MILLISECONDS,
                property.getReadTimeOut(),
                TimeUnit.MILLISECONDS,
                true
        );

        log.info("Prepared request options for uzCard integration feign client: {}", rOptions);
        return rOptions;
    }
    
}
