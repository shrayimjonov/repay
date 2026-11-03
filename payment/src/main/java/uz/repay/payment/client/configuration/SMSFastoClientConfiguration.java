package uz.repay.payment.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.payment.model.property.FastoSMSProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class SMSFastoClientConfiguration {
    private final FastoSMSProperty property;

    @Bean
    public Logger.Level smsFastoFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder smsFastoErrorDecoder() {
        return new ErrorDecoder.Default();
    }


    @Bean
    public Request.Options smsFastoRequestTimeOuts() {
        return new Request.Options(
                property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true
        );
    }
}
