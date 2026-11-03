package uz.nodir.repayautoconfigure.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;


@Slf4j
public class RetryCustomListener implements RetryListener {

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        log.info("Retry operation started with context: {}", context);
        return true; // Продолжаем выполнение
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        log.warn("Retry attempt failed. Attempt count: {}, and message: {}", context.getRetryCount(), throwable.getMessage());
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (throwable == null) {
            log.info("Retry operation completed successfully with context: {}", context);
        } else {
            log.error("Retry operation completed with error: {}", throwable.getMessage());
        }
    }
}
