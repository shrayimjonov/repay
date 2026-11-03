package uz.repay.autodebit.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.autodebit.model.property.CardProperty;

import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class CardServiceFeignConfiguration {
    private final CardProperty property;

    @Bean
    public ErrorDecoder errorDecoderCard() {
        return new ErrorDecoder.Default();
    }

    @Bean
    public Logger.Level feignLoggerLevelCard() {
        return Logger.Level.BASIC;
    }


    @Bean
    public Request.Options getOptionsCard() {
        return new Request.Options(
                property.getConnectTimeOut(),
                TimeUnit.MILLISECONDS,
                property.getReadTimeOut(),
                TimeUnit.MILLISECONDS,
                true);
    }
}
