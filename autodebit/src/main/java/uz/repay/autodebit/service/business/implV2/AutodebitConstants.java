package uz.repay.autodebit.service.business.implV2;

public final class AutodebitConstants {
    public static final String AUTODEBIT_TOPIC = "autodebit-topic-v2";

    public static final String CYCLE_CONSUMER_GROUP_ID = "auto-debit-cycle-v1";

    public static final String CONSUMER_CONCURRENCY = "10";

    public static final long PROCESSING_INTERVAL_MILLIS = 600_000L;

    public static final long CYCLE_FIXED_RATE_MILLIS = 600_000L;
    public static final long MAINTENANCE_FIXED_RATE_MILLIS = 3_600_000L;

    public static final int TARGET_DEBTORS_PER_MINUTE = 10_000;

    public static final int INSTANCE_COUNT = 10;

    public static final double RATE_LIMITER_BURST_SECONDS = 30D;

    public static final int MAX_IN_FLIGHT_DEBITS = 200;

    public static final long UNKNOWN_OUTCOME_COOLDOWN_MILLIS = 1_800_000L;

    public static final long STALE_PROCESSING_AFTER_MILLIS = 900_000L;

    public static final int ATTEMPT_RETENTION_DAYS = 1;

    public static final long PUBLISH_CHUNK_JOIN_TIMEOUT_SECONDS = 60L;

    public static final long BATCH_JOIN_TIMEOUT_SECONDS = 240L;

    public static final boolean AUTO_PAYMENT_ACTIVE_BY_DEFAULT = true;

    public static final long STATE_CACHE_TTL_MILLIS = 3_000L;

    public static final int DEBTOR_LOAD_PAGE_SIZE = 5_000;

    private AutodebitConstants() {

    }
}
