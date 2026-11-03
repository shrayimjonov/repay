package uz.davrbank.repay.loggingrest.dto;


public record ActiveUserIp(
        String username,
        String ip,
        String macAddress,
        String lastActionDate
) {
}
