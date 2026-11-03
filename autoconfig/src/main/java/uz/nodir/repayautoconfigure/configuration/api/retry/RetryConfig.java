package uz.nodir.repayautoconfigure.configuration.api.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import uz.nodir.repayautoconfigure.listener.RetryCustomListener;

@Configuration
@ConditionalOnClass(RetryTemplate.class)
@EnableRetry
@Slf4j
public class RetryConfig {

    @Bean
    @ConditionalOnMissingBean
    public RetryPolicy customRetryPolicy() {
        var attemptsCount = 3;
        log.info("Build Spring retry policy with {} default attempts count", attemptsCount);
        return new SimpleRetryPolicy(attemptsCount); // Retry up to 5 times
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(2);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    @Bean(name = "simpleResponseTimeOutRetryer")
    public RetryTemplate simpleResponseTimeOutRetryer() {
        RetryTemplate template = new RetryTemplate();

        // Политика количества повторных попыток
        template.setRetryPolicy(new RepayRetryPolicy(3));


        // Политика задержек (экспоненциальный рост)
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
     //   backOffPolicy.setInitialInterval(1000); // 1 секунда
        backOffPolicy.setMultiplier(2.0); // Увеличивать в 2 раза каждую попытку
        backOffPolicy.setMaxInterval(15_000); // Максимальная задержка 15 секунд
        template.setBackOffPolicy(backOffPolicy);

        template.registerListener(new RetryCustomListener());

        log.info("Build retryable template: {}", template);
        return template;
    }
}
