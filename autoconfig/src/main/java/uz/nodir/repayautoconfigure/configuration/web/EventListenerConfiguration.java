package uz.nodir.repayautoconfigure.configuration.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
@Slf4j
@RequiredArgsConstructor
public class EventListenerConfiguration {

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(25);
        taskExecutor.setThreadNamePrefix("RPY_EVENT_");
        taskExecutor.initialize();

        eventMulticaster.setTaskExecutor(taskExecutor);
        eventMulticaster.setErrorHandler(
                throwable -> log.info("Taken throw while processing event in async mode with message: {} ",
                        throwable.getMessage())
        );
        return eventMulticaster;
    }
}
