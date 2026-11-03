package uz.repay.autodebit.service.business.v5;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.autodebit.service.business.implV2.AutodebitConstants;

@Service
@Slf4j
public class DebitThroughputLimiter {
    private final Object lock = new Object();
    private final double permitsPerSecond;
    private final double maxPermits;
    private double storedPermits;
    private long lastRefillNanos;

    public DebitThroughputLimiter() {
        this.permitsPerSecond = (double) AutodebitConstants.TARGET_DEBTORS_PER_MINUTE
                / 60D / Math.max(1, AutodebitConstants.INSTANCE_COUNT);
        this.maxPermits = Math.max(1D, permitsPerSecond * AutodebitConstants.RATE_LIMITER_BURST_SECONDS);
        this.storedPermits = maxPermits;
        this.lastRefillNanos = System.nanoTime();

        log.info("event=autodebit.v5.limiter.created permitsPerSecond={} maxBurst={}", permitsPerSecond, maxPermits);
    }

    public boolean acquire(long deadlineEpochMillis) throws InterruptedException {
        while (true) {
            long waitMillis;
            synchronized (lock) {
                refill();
                if (storedPermits >= 1D) {
                    storedPermits -= 1D;
                    return true;
                }
                waitMillis = (long) Math.ceil((1D - storedPermits) / permitsPerSecond * 1000D);
            }

            var now = System.currentTimeMillis();
            if (deadlineEpochMillis > 0 && now + waitMillis > deadlineEpochMillis) {
                return false;
            }
            Thread.sleep(Math.clamp(waitMillis, 1L, 1_000L));
        }
    }

    private void refill() {
        var now = System.nanoTime();
        var elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000D;
        if (elapsedSeconds <= 0D) {
            return;
        }

        storedPermits = Math.min(maxPermits, storedPermits + (elapsedSeconds * permitsPerSecond));
        lastRefillNanos = now;
    }
}