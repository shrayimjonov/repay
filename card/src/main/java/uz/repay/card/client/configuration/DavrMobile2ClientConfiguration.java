package uz.repay.card.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.card.model.property.DM2Property;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class DavrMobile2ClientConfiguration {
    private final DM2Property property;

    @Bean
    Logger.Level davrMobile2FeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder davrMobile2ErrorDecoder() {
        return new ErrorDecoder.Default();
    }


    @Bean
    public Request.Options davrMobile2RequestTimeOuts() {
        return new Request.Options(property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true);
    }
}
