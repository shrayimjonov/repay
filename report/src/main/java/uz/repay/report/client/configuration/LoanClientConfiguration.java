package uz.repay.report.client.configuration;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import uz.repay.report.client.interceptor.LoanBearerRequestInterceptor;
import uz.repay.report.model.property.LoanProperty;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class LoanClientConfiguration {
    private final LoanProperty property;

    @Bean
    public Logger.Level loanFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder loanErrorDecoder() {
        return new ErrorDecoder.Default();
    }


    @Bean
    public RequestInterceptor requestInterceptor() {
        return new LoanBearerRequestInterceptor();
    }

    @Bean
    public Request.Options loanRequestTimeOuts() {
        return new Request.Options(
                property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true
        );
    }
}
