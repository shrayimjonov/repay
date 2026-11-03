package uz.repay.card.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.card.model.property.HumoProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class HumoClientConfiguration {
    private final HumoProperty property;

    @Bean
    Logger.Level humoFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder humoErrorDecoder() {
        return new ErrorDecoder.Default();
    }

    @Bean
    public Request.Options humoRequestTimeOuts() {
        return new Request.Options(property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true);
    }
}
