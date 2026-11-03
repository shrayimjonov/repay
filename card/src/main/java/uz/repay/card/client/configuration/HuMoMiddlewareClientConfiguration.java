package uz.repay.card.client.configuration;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import uz.repay.card.client.decoder.HuMoMiddlewareErrorDecoder;
import uz.repay.card.model.property.HumoProperty;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class HuMoMiddlewareClientConfiguration {
    private final HumoProperty property;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";

    @Bean
    public Logger.Level hmFeignLoggerLevel() {
        var logger = Logger.Level.FULL;
        log.info("Prepared huMo middleware logger: {}", logger);
        return logger;
    }

    @Bean
    public ErrorDecoder hmErrorDecoder() {
        var errorDecoder = new HuMoMiddlewareErrorDecoder();
        log.info("Built huMo middleware error decoder: {}", errorDecoder);
        return errorDecoder;
    }


    @Bean
    public Request.Options hmRequestTimeOuts() {
        var ro = new Request.Options(
                property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true);
        log.info("Request options for huMo middleware: {}", ro);
        return ro;
    }


    @Bean
    @Profile("prod")
    public RequestInterceptor hmRequestInterceptor() {
        log.info("HuMo middleware using token last letters: {}", property.getToken().substring(145));
        return requestTemplate -> requestTemplate.header(AUTHORIZATION_HEADER, String.format("%s %s", TOKEN_TYPE, property.getToken()));
    }


}
