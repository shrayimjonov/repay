package uz.repay.payment.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.payment.model.property.UzCardIntegrationProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class UzCardIntegrationFeignConfiguration {
    private final UzCardIntegrationProperty property;

    @Bean
    public ErrorDecoder errorDecoderUzCard() {
        return new ErrorDecoder.Default();
    }

    @Bean
    public Logger.Level feignLoggerLevelUzCard() {
        return Logger.Level.FULL;
    }


    @Bean
    public Request.Options getOptionsUzCard() {
        return new Request.Options(
                property.getConnectTimeOut(),
                TimeUnit.MILLISECONDS,
                property.getReadTimeOut(),
                TimeUnit.MILLISECONDS,
                true);
    }
}
