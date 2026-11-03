package uz.repay.autodebit.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.autodebit.model.property.PaymentProperty;

import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class PaymentServiceFeignConfiguration {
    private final PaymentProperty property;

    @Bean
    public ErrorDecoder errorDecoderPayment() {
        return new ErrorDecoder.Default();
    }

    @Bean
    public Logger.Level feignLoggerLevelPayment() {
        return Logger.Level.BASIC;
    }


    @Bean
    public Request.Options getOptionsPayment() {
        return new Request.Options(
                property.getConnectTimeOut(),
                TimeUnit.MILLISECONDS,
                property.getReadTimeOut(),
                TimeUnit.MILLISECONDS,
                true);
    }
}
