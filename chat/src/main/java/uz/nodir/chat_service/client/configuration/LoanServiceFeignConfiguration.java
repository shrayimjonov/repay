package uz.nodir.chat_service.client.configuration;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.nodir.chat_service.dto.property.LoanProperty;

import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class LoanServiceFeignConfiguration {
    private final LoanProperty property;

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
