package uz.nodir.notification.client.configuration;

import feign.Logger;
import feign.Request;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import uz.nodir.notification.model.property.SMSProperty;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class SMSClientConfiguration {
    private final SMSProperty property;

    @Bean
    public Logger.Level smsFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder smsErrorDecoder() {
        return new ErrorDecoder.Default();
    }

    @Bean
    public BasicAuthRequestInterceptor smsBasicAuthRequestInterceptor() {
        log.info("SMS Client using username: {}", property.getUsername());
        return new BasicAuthRequestInterceptor(property.getUsername(), property.getPassword());
    }

    @Bean
    public Request.Options smsRequestTimeOuts() {
        log.info("SMS Client readTimeOut: {} and connectTimeOut: {} seconds", property.getReadTimeOut(), property.getConnectTimeOut());
        return new Request.Options(
                property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true
        );
    }

}
