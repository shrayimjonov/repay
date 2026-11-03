package uz.nodir.scoring.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import uz.nodir.scoring.model.property.HuMoGatewayProperty;
import uz.nodir.scoring.model.property.MonitoringProperty;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class HuMoGatewayFeignConfiguration {
    private final HuMoGatewayProperty property;

    @Bean
    public ErrorDecoder errorDecoderHuMoGateway() {
        ErrorDecoder.Default eDecoder = new ErrorDecoder.Default();
        log.info("Prepared error decoder for huMo gateway feign client: {}", eDecoder);
        return eDecoder;
    }

    @Bean
    public Logger.Level feignLoggerLevelHuMoGateway() {
        Logger.Level level = Logger.Level.FULL;
        log.info("Prepared logger level for huMo gateway feign client: {}", level);
        return level;
    }


    @Bean
    public Request.Options getOptionsHuMoGateway() {
        Request.Options rOptions = new Request.Options(
                property.getConnectTimeOut(),
                TimeUnit.MILLISECONDS,
                property.getReadTimeOut(),
                TimeUnit.MILLISECONDS,
                true
        );

        log.info("Prepared request options for huMo gateway feign client: {}", rOptions);
        return rOptions;
    }
    
}
