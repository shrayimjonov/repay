package uz.repay.payment.client.configuration;

import feign.Logger;
import feign.Request;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.payment.model.property.HumoGatewayProperty;
import uz.repay.payment.model.property.UzCardIntegrationProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class HuMoGatewayFeignConfiguration {
    private final HumoGatewayProperty property;

    @Bean
    public ErrorDecoder errorDecoderHG() {
        return new ErrorDecoder.Default();
    }

    @Bean
    public Logger.Level feignLoggerLevelHG() {
        return Logger.Level.FULL;
    }


    @Bean
    public Request.Options getOptionsHG() {
        return new Request.Options(
                property.getConnectTimeOut(),
                TimeUnit.MILLISECONDS,
                property.getReadTimeOut(),
                TimeUnit.MILLISECONDS,
                true);
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptorHG(){
        return new BasicAuthRequestInterceptor(property.getUsername(), property.getPassword());
    }
}
