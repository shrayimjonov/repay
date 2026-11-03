package uz.repay.payment.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.payment.model.property.CardServiceProperty;
import uz.repay.payment.model.property.NCIBillingProperty;

import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class NCIBillingFeignConfiguration {
    private final NCIBillingProperty property;

    @Bean
    public ErrorDecoder errorDecoderNCIBilling() {
        return new ErrorDecoder.Default();
    }

    @Bean
    public Logger.Level feignLoggerLevelNCIBilling() {
        return Logger.Level.FULL;
    }


    @Bean
    public Request.Options getOptionsNCIBilling() {
        return new Request.Options(
                property.getConnectTimeOut(),
                TimeUnit.MILLISECONDS,
                property.getReadTimeOut(),
                TimeUnit.MILLISECONDS,
                true);
    }
}
