package uz.repay.autodebit.service.business.implV2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class MonitoringService {
    private final Executor executor;

    public MonitoringService(@Qualifier("autoPaymentTaskExecutor") Executor executor) {
        this.executor = executor;
    }


    @Async
    public void runProcess(PaymentWorkerV2 z, boolean isInsolvent) {
        if (isInsolvent)
            log.info("Starting insolvent worker: {} with id: {}", z.getBranch(), z.getWorkerId());
        else
            log.info("Starting worker: {} with id: {}", z.getBranch(), z.getWorkerId());

        CompletableFuture
                .supplyAsync(
                        z::call,
                        executor
                )
                .thenApplyAsync(s -> {
                            log.info("""

                                             Success payments: {}
                                             Failed payments: {}
                                            """,
                                    s.getSuccessList().size(),
                                    s.getFailedList().size()
                            );

                            if (isInsolvent)
                                log.info("Finishing insolvent worker: {} with id: {}", z.getBranch(), z.getWorkerId());
                            else
                                log.info("Finishing worker: {} with id: {}", z.getBranch(), z.getWorkerId());
                            return s;
                        },
                        executor
                )
                .exceptionally(error -> {
                    log.error("Worker {} failed", z.getWorkerId(), error);
                    return null;
                });
    }

}
