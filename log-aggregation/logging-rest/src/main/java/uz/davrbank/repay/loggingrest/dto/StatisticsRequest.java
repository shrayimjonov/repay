package uz.davrbank.repay.loggingrest.dto;

import java.time.LocalDate;

public record StatisticsRequest(
        LocalDate date
) {
}
