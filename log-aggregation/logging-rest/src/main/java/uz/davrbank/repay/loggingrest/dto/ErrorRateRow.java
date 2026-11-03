package uz.davrbank.repay.loggingrest.dto;

public record ErrorRateRow(
        String service,
        String endpoint,
        long total,
        long failures,
        double errorPct,
        double p99Ms
) {
}
