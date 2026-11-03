package uz.repay.card.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.card.model.property.LoanServiceProperty;

import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class LoanServiceFeignConfiguration {
    private final LoanServiceProperty property;

    @Bean
    public ErrorDecoder errorDecoderLoan() {
        return new ErrorDecoder.Default();
    }

    @Bean
    public Logger.Level feignLoggerLevelLoan() {
        return Logger.Level.FULL;
    }


    @Bean
    public Request.Options getOptionsLoan() {
        return new Request.Options(
                property.getConnectTimeOut(),
                TimeUnit.MILLISECONDS,
                property.getReadTimeOut(),
                TimeUnit.MILLISECONDS,
                true);
    }
}
