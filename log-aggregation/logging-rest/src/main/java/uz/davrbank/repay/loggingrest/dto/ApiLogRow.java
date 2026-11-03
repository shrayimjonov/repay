package uz.davrbank.repay.loggingrest.dto;

public record ApiLogRow(
        String eventTime,
        String traceId,
        String spanId,
        String parentSpanId,
        String direction,
        String service,
        String username,
        String clientIp,
        String endpoint,
        String httpMethod,
        Integer statusCode,
        Boolean success,
        Long durationMs,
        Integer attempt,
        String errorMessage,
        String requestBody,
        String responseBody
) {
}
