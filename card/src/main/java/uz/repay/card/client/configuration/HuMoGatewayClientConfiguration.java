package uz.repay.card.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import uz.repay.card.model.property.HuMoGatewayProperty;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class HuMoGatewayClientConfiguration {
    private final HuMoGatewayProperty property;

    @Bean
    public Logger.Level huMoGatewLoggerLevel() {
        var logger = Logger.Level.FULL;
        log.info("Logger prepared for huMo Gateway: {}", logger);
        return logger;
    }

    @Bean
    public ErrorDecoder huMoGatewayErrorDecoder() {
        var decoder = new ErrorDecoder.Default();
        log.info("HuMo gateway error decoder is: {}", decoder);
        return decoder;
    }

    @Bean
    public Request.Options huMoGatewayRequestTimeOut() {
        var ro = new Request.Options(
                property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true
        );
        log.info("HuMo gateway built request options: {}", ro);
        return ro;
    }
}
