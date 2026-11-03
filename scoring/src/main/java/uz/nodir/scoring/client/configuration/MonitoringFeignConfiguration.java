package uz.nodir.scoring.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import uz.nodir.scoring.model.property.MonitoringProperty;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class MonitoringFeignConfiguration {
    private final MonitoringProperty property;

    @Bean
    public ErrorDecoder errorDecoderMonitoring() {
        ErrorDecoder.Default eDecoder = new ErrorDecoder.Default();
        log.info("Prepared error decoder for card monitoring feign client: {}", eDecoder);
        return eDecoder;
    }

    @Bean
    public Logger.Level feignLoggerLevelMonitoring() {
        Logger.Level level = Logger.Level.FULL;
        log.info("Prepared logger level for card monitoring feign client: {}", level);
        return level;
    }


    @Bean
    public Request.Options getOptionsMonitoring() {
        Request.Options rOptions = new Request.Options(
                property.getConnectTimeOut(),
                TimeUnit.MILLISECONDS,
                property.getReadTimeOut(),
                TimeUnit.MILLISECONDS,
                true);

        log.info("Prepared request options for card monitoring feign client: {}", rOptions);
        return rOptions;
    }
    
}
