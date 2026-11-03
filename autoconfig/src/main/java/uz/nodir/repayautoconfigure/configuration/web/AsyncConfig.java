package uz.nodir.repayautoconfigure.configuration.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean
    @ConditionalOnMissingBean
    public Executor taskExecutor() {
        // Async thread pool configuration
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("rpy_def_async_");
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(1000);
        executor.initialize();
        return executor;
    }

}
