package uz.nodir.repayautoconfigure.configuration.api.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import uz.repay.common.model.dto.response.core.SimpleResponse;


@Slf4j
public class RepayRetryPolicy implements RetryPolicy {
    public final static String RESPONSE_ATTRIBUTE = "last_simple_response";
    public static final String ATTEMPT_COUNT_ATTRIBUTE = "attempt_count";
    private final int maxAttempts;


    public RepayRetryPolicy(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    @Override
    public boolean canRetry(RetryContext retryContext) {
        int attemptCount = retryContext.getAttribute(ATTEMPT_COUNT_ATTRIBUTE) != null ? (int) retryContext.getAttribute(ATTEMPT_COUNT_ATTRIBUTE) : 0 ;

        log.info("Checking can retry. Attempt: {}/{}", attemptCount, maxAttempts);

        if (attemptCount >= maxAttempts) {
            return false;
        }

        try {
            var lastResponse = (SimpleResponse<?>) retryContext.getAttribute(RESPONSE_ATTRIBUTE);
            log.info("Last simple response: {}", lastResponse);

            // Если ответа нет - продолжаем попытки
            if (lastResponse == null) {
                log.info("Last response is null. Allowing retry if attempt count ({}) is less than max attempts ({}).", attemptCount, maxAttempts);
                retryContext.setAttribute(ATTEMPT_COUNT_ATTRIBUTE, attemptCount + 1);
                return true;
            }

            boolean shouldRetry = lastResponse.isTimeout();
            if (shouldRetry) {
                retryContext.setAttribute(ATTEMPT_COUNT_ATTRIBUTE, attemptCount + 1);
            }

            return shouldRetry;
        } catch (Exception e) {
            log.error("Exit checking retry with false value. Cause taking an error with message: {}", e.getMessage());
            retryContext.setAttribute(ATTEMPT_COUNT_ATTRIBUTE, attemptCount + 1);
            return false;
        }
    }

    @Override
    public RetryContext open(RetryContext parent) {
        RetryContextSupport retryContext = new RetryContextSupport(parent);
        log.info("Open new retry context: {}", retryContext);

        retryContext.setAttribute(ATTEMPT_COUNT_ATTRIBUTE, 0); // Устанавливаем счетчик попыток
        return retryContext;
    }

    @Override
    public void close(RetryContext retryContext) {
        log.info("Close retry context: {}", retryContext);
    }

    @Override
    public void registerThrowable(RetryContext retryContext, Throwable throwable) {
        log.info("Registering throwable with message: {}", throwable.getMessage());

        ((RetryContextSupport) retryContext).registerThrowable(throwable);
    }
}
