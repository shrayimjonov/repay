package uz.repay.payment.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.payment.model.property.LoanServiceProperty;
import uz.repay.payment.model.property.VisaIntegrationProperty;

import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class VisaIntegrationFeignConfiguration {
    private final VisaIntegrationProperty property;

    @Bean
    public ErrorDecoder visaErrorDecoder() {
        return new ErrorDecoder.Default();
    }

    @Bean
    public Logger.Level visaFeignLoggerLevel() {
        return Logger.Level.FULL;
    }


    @Bean
    public Request.Options visaGetOptions() {
        return new Request.Options(
                property.getConnectTimeOut(),
                TimeUnit.MILLISECONDS,
                property.getReadTimeOut(),
                TimeUnit.MILLISECONDS,
                true);
    }
}
