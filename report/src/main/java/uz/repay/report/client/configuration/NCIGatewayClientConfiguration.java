package uz.repay.report.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.report.model.property.NCIGatewayProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class NCIGatewayClientConfiguration {
    private final NCIGatewayProperty property;

    @Bean
    public Logger.Level NCIGatewayFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder NCIGatewayErrorDecoder() {
        return new ErrorDecoder.Default();
    }


    @Bean
    public Request.Options NCIGatewayRequestTimeOuts() {
        return new Request.Options(
                property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true
        );
    }
}
