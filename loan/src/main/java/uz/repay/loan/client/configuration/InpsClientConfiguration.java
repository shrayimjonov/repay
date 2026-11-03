package uz.repay.loan.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.loan.model.property.InpsProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class InpsClientConfiguration {
    private final InpsProperty property;

    @Bean
    public Logger.Level inpsFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder inpsErrorDecoder() {
        return new ErrorDecoder.Default();
    }


    @Bean
    public Request.Options inpsRequestTimeOuts() {
        return new Request.Options(property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true);
    }
}
