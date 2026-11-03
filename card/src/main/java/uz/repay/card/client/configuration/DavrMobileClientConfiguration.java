package uz.repay.card.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.card.model.property.DMProperty;
import uz.repay.card.model.property.UzCardProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class DavrMobileClientConfiguration {
    private final DMProperty property;

    @Bean
    Logger.Level davrMobileFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder davrMobileErrorDecoder() {
        return new ErrorDecoder.Default();
    }


    @Bean
    public Request.Options davrMobileRequestTimeOuts() {
        return new Request.Options(property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true);
    }
}
