package uz.repay.card.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.card.model.property.FastoProperty;
import uz.repay.card.model.property.SmsProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class SMSFastoClientConfiguration {
    private final FastoProperty property;

    @Bean
    Logger.Level smsFastoFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder smsFastoErrorDecoder() {
        return new ErrorDecoder.Default();
    }


    @Bean
    public Request.Options smsFastoRequestTimeOuts() {
        return new Request.Options(
                property.getSms().getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getSms().getReadTimeOut(), TimeUnit.MILLISECONDS,
                true
        );
    }
}
