package uz.repay.card.client.configuration;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import uz.repay.card.model.property.ElmaProperty;

import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
public class ElmaClientConfiguration {
    private final ElmaProperty property;
    private final static String ELMA_HEADER_CLIENT_KEY = "IsbmApi-Client";
    private final static String ELMA_HEADER_CLIENT_VALUE = "M2A";

    @Bean
    public Logger.Level elmaFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder elmaErrorDecoder() {
        return new ErrorDecoder.Default();
    }


    @Bean
    public Request.Options elmaRequestTimeOuts() {
        return new Request.Options(
                property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true
        );
    }

    @Bean
    public RequestInterceptor elmaRequestInterceptor() {
        return requestTemplate -> requestTemplate.header(ELMA_HEADER_CLIENT_KEY, ELMA_HEADER_CLIENT_VALUE);
    }

    @Bean
    public BasicAuthRequestInterceptor elmaBasicAuthRequestInterceptor() {
        log.info("Basic user taken to elma request: {}", property.getUsername());
        return new BasicAuthRequestInterceptor(property.getUsername(), property.getPassword());
    }
}
