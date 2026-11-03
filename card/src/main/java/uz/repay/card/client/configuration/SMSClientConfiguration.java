package uz.repay.card.client.configuration;

import feign.Logger;
import feign.Request;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.card.model.property.SmsProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class SMSClientConfiguration {
    private final SmsProperty property;

    @Bean
    Logger.Level smsFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder smsErrorDecoder() {
        return new ErrorDecoder.Default();
    }

    @Bean
    public BasicAuthRequestInterceptor smsBasicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(property.getUsername(), property.getPassword());
    }

    @Bean
    public Request.Options smsRequestTimeOuts() {
        return new Request.Options(property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true);
    }
}
