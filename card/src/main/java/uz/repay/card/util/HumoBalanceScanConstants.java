package uz.repay.card.util;

public final class HumoBalanceScanConstants {
    public static final int HUMO_BALANCE_SCAN_TARGET_CARDS_PER_MINUTE = 10_000;
    public static final int HUMO_BALANCE_SCAN_TOPIC_PARTITIONS = 100;
    public static final int HUMO_BALANCE_SCAN_MAX_IN_FLIGHT_PER_INSTANCE = 1;
    public static final int HUMO_BALANCE_SCAN_MAX_CARDS_PER_INSTANCE_PER_RUN = 500;
    public static final long HUMO_BALANCE_SCAN_FIXED_RATE_MILLIS = 60_000L;
    public static final long HUMO_BALANCE_REGISTRY_SYNC_FIXED_RATE_MILLIS = 120_000L;
    public static final long HUMO_BALANCE_REGISTRY_SYNC_INITIAL_DELAY_MILLIS = 10_000L;
    public static final long HUMO_BALANCE_DETECTED_KAFKA_SEND_TIMEOUT_MILLIS = 5_000L;
    public static final long MINIMUM_HUMO_BALANCE_FOR_AUTODEBIT = 5000_00L;

    public static final String HUMO_BALANCE_SCAN_TOPIC = "card-humo-balance-scan";
    public static final String HUMO_BALANCE_DETECTED_TOPIC = "autodebit-humo-balance-detected";
    public static final String HUMO_BALANCE_SCAN_CONSUMER_GROUP_ID = "card-humo-balance-scan-consumer";
    public static final String HUMO_BALANCE_DETECTED_CONSUMER_GROUP_ID = "card-humo-balance-detected-consumer";
    public static final String HUMO_BALANCE_SCAN_CONSUMER_CONCURRENCY = "10";

    private HumoBalanceScanConstants() {
    }
}
