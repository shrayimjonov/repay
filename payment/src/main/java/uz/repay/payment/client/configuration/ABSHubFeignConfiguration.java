package uz.repay.payment.client.configuration;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import uz.repay.payment.model.property.AbsHubProperty;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class ABSHubFeignConfiguration {
    private final AbsHubProperty property;

    @Bean
    public ErrorDecoder errorDecoderABSHub() {
        ErrorDecoder.Default eDecoder = new ErrorDecoder.Default();
        log.info("Prepared error decoder for abs hub: {}", eDecoder);
        return eDecoder;
    }

    @Bean
    public Logger.Level feignLoggerLevelABSHub() {
        Logger.Level level = Logger.Level.FULL;
        log.info("Prepared logger level for abs hub: {}", level);
        return level;
    }


    @Bean
    public Request.Options getOptionsABSHub() {
        Request.Options rOptions = new Request.Options(
                property.getConnectTimeOut(),
                TimeUnit.MILLISECONDS,
                property.getReadTimeOut(),
                TimeUnit.MILLISECONDS,
                true);

        log.info("Prepared request options for abs hub: {}", rOptions);
        return rOptions;
    }

    @Bean
    public RequestInterceptor requestInterceptorABSHub() {
        log.info("Set auth to abs hub request interceptor with login: {}", property.getUsername());
        return new BasicAuthRequestInterceptor(
                property.getUsername(),
                property.getPassword()
        );
    }
}
