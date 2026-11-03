package uz.repay.autodebit.model.entity;

public enum DebitAttemptStatus {

    PROCESSING,
    COMPLETED,
    FAILED,
    FAILED_UNKNOWN,
    EXPIRED_UNKNOWN,
    SKIPPED_COOLDOWN,
    SKIPPED_INACTIVE,
    STALE_DROPPED
}
