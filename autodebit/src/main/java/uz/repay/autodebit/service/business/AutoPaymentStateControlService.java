package uz.repay.autodebit.service.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import uz.repay.autodebit.service.business.implV2.AutodebitConstants;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutoPaymentStateControlService {

    private static final String ACTIVE_KEY = "auto-debit:v2:active";

    private final StringRedisTemplate redisTemplate;

    private volatile Boolean cachedValue;
    private volatile long cacheExpiresAtMillis;

    public boolean isActive() {
        var now = System.currentTimeMillis();
        var cached = cachedValue;
        if (cached != null && now < cacheExpiresAtMillis) {
            return cached;
        }

        try {
            var value = redisTemplate.opsForValue().get(ACTIVE_KEY);

            boolean active;
            if (value == null) {
                redisTemplate.opsForValue().setIfAbsent(ACTIVE_KEY,
                        Boolean.toString(AutodebitConstants.AUTO_PAYMENT_ACTIVE_BY_DEFAULT));
                active = AutodebitConstants.AUTO_PAYMENT_ACTIVE_BY_DEFAULT;
            } else if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                log.error("Invalid auto payment state in Redis. key: {}, value: {}", ACTIVE_KEY, value);
                active = false;
            } else {
                active = Boolean.parseBoolean(value);
            }

            cachedValue = active;
            cacheExpiresAtMillis = now + AutodebitConstants.STATE_CACHE_TTL_MILLIS;
            return active;
        } catch (RuntimeException error) {
            var lastKnown = cachedValue;
            if (lastKnown != null) {
                log.warn("Failed to read auto payment state from Redis - using last known value: {}", lastKnown, error);
                return lastKnown;
            }
            log.error("Failed to read auto payment state from Redis and no cached value exists. Treating as disabled.", error);
            return false;
        }
    }

    public void setActive(boolean requestedState) {
        try {
            redisTemplate.opsForValue().set(ACTIVE_KEY, Boolean.toString(requestedState));
            cachedValue = requestedState;
            cacheExpiresAtMillis = System.currentTimeMillis() + AutodebitConstants.STATE_CACHE_TTL_MILLIS;
            log.info("event=autodebit.state.changed active={} (cluster-wide, other instances follow within {}ms)",
                    requestedState, AutodebitConstants.STATE_CACHE_TTL_MILLIS);
        } catch (RuntimeException error) {
            log.error("Failed to update auto payment state in Redis. requestedState: {}", requestedState, error);
            throw error;
        }
    }
}
