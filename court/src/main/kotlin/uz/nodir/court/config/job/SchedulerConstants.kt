package uz.nodir.court.config.job

class SchedulerConstants {
    companion object {
        const val RETRIES = "retries";
        const val MAX_ALLOWED_RETRY_COUNT = "max_allowed_retry_count";
        const val CURRENT_RETRY_COUNT = "current_retry_count";
        const val RETRY_DELAY = "retry_delay";

        const val MANUAL_ADDED_GROUP_ID = "courtServiceManual"
    }
}