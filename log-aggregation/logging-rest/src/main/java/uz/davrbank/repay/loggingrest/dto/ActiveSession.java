package uz.davrbank.repay.loggingrest.dto;


public record ActiveSession(
        String username,
        String ip,
        String firstName,
        String lastName,
        String loginTime,
        String lastActionDate,
        String expiresAt,
        Long requestCount
) {
}