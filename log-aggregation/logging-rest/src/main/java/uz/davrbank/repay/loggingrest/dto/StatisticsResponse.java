package uz.davrbank.repay.loggingrest.dto;

import java.time.LocalDate;

public record StatisticsResponse(
        LocalDate date,
        long requestCount,
        long successCount,
        long errorCount
) {
}
