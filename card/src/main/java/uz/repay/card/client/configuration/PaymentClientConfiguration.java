package uz.repay.card.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.card.model.property.DM2Property;
import uz.repay.card.model.property.PaymentProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class PaymentClientConfiguration {
    private final PaymentProperty property;

    @Bean
    Logger.Level paymentFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder paymentErrorDecoder() {
        return new ErrorDecoder.Default();
    }


    @Bean
    public Request.Options paymentRequestTimeOuts() {
        return new Request.Options(property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true);
    }
}
