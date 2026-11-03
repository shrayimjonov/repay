package uz.repay.card.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.card.model.property.UzCardProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class UzCardIntegrationClientConfiguration {
    private final UzCardProperty property;

    @Bean
    Logger.Level uzcardFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder uzcardErrorDecoder() {
        return new ErrorDecoder.Default();
    }


    @Bean
    public Request.Options uzcardRequestTimeOuts() {
        return new Request.Options(property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true);
    }
}
