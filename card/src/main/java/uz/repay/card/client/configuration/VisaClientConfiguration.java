package uz.repay.card.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import uz.repay.card.model.property.HumoProperty;
import uz.repay.card.model.property.VisaProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class VisaClientConfiguration {
    private final VisaProperty property;

    @Bean
    public Logger.Level visaFeignLoggerLevel() {

        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder visaErrorDecoder() {
        return new ErrorDecoder.Default();
    }

    @Bean
    public Request.Options visaRequestTimeOuts() {
        log.info("Visa request options, readTimeOut: {} connectTimeOut: {}", property.getReadTimeOut(), property.getConnectTimeOut());

        return new Request.Options(property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true);
    }
}
