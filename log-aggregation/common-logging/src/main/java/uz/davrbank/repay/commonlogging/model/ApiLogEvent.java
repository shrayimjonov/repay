package uz.davrbank.repay.commonlogging.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiLogEvent {

    @JsonProperty("event_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    ZonedDateTime eventTime;

    @JsonProperty("trace_id")
    String traceId;

    @JsonProperty("span_id")
    String spanId;

    @JsonProperty("parent_span_id")
    String parentSpanId;

    @JsonProperty("direction")
    Direction direction;

    @JsonProperty("service")
    String service;

    @JsonProperty("username")
    String username;

    @JsonProperty("client_ip")
    String clientIp;

    @JsonProperty("mac_address")
    String mac_address;

    @JsonProperty("endpoint")
    String endpoint;

    @JsonProperty("params")
    Map<String, String> params;

    @JsonProperty("http_method")
    String httpMethod;

    @JsonProperty("status_code")
    Integer statusCode;

    @JsonProperty("success")
    Boolean success;

    @JsonProperty("duration_ms")
    Long durationMs;

    @JsonProperty("attempt")
    Integer attempt;

    @JsonProperty("error_message")
    String errorMessage;

    @JsonProperty("request_body")
    String requestBody;

    @JsonProperty("response_body")
    String responseBody;
}
