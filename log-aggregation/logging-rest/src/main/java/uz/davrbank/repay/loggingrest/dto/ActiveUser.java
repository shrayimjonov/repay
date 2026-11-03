package uz.davrbank.repay.loggingrest.dto;


public record ActiveUser(
        String username,
        String lastActionDate
) {
}
