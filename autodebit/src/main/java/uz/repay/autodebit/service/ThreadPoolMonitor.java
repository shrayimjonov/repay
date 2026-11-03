package uz.repay.autodebit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThreadPoolMonitor {

    private final Executor asyncConfigForAD;

    @Scheduled(fixedDelay = 30000)
    public void monitor() {
        ThreadPoolTaskExecutor executor =
                (ThreadPoolTaskExecutor) asyncConfigForAD;

        log.info("=== Thread Pool Status ===");
        log.info("Active threads  : {}", executor.getActiveCount());
        log.info("Pool size       : {}", executor.getPoolSize());
        log.info("Queue size      : {}", executor.getThreadPoolExecutor()
                .getQueue().size());
        log.info("Completed tasks : {}", executor.getThreadPoolExecutor()
                .getCompletedTaskCount());
        log.info("==========================");
    }
}
