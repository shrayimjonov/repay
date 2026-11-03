package uz.repay.loan.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.loan.model.property.NCIGateProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class NCIBillingClientConfiguration {
    private final NCIGateProperty property;

    @Bean
    public Logger.Level absFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder absErrorDecoder() {
        return new ErrorDecoder.Default();
    }


    @Bean
    public Request.Options absRequestTimeOuts() {
        return new Request.Options(property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true);
    }
}
