package uz.repay.loan.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.loan.model.property.ClientServiceProperty;

import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class ClientServiceFeignConfiguration {
    private final ClientServiceProperty property;

    @Bean
    public Logger.Level clientFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder clientErrorDecoder() {
        return new ErrorDecoder.Default();
    }


    @Bean
    public Request.Options clientRequestTimeOuts() {
        return new Request.Options(
                property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true
        );
    }
}
