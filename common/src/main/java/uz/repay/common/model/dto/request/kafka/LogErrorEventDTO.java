package uz.repay.common.model.dto.request.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogErrorEventDTO {
    @JsonProperty("service")
    private String service;

    @JsonProperty("method")
    private String method;

    @JsonProperty("message")
    private String message;

    @JsonProperty("date_time")
    private LocalDateTime dateTime;

    public String toJson() {
        return """
                {
                  "service": "%s",
                  "method": "%s",
                  "message": "%s",
                  "date_time": "%s"
                }""".formatted(
                this.service,
                this.method,
                this.message,
                this.dateTime
        );
    }
}
