package uz.nodir.notification.model.dto.push.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.mongo.entity.Push;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushResponseDTO {
    @JsonProperty("id")
    @Schema(name = "id", example = "18495c68-2fa0-4813-b1c4-5c8b2463212b", description = "id field")
    private UUID id;
    @JsonProperty("created_time")
    @Schema(name = "createdTime", example = "2024-05-21 10:56:42.424", description = "created date and time")
    private LocalDateTime createdTime;
    @JsonProperty("loan_ext_id")
    @Schema(name = "loan_ext_id", example = "0098127386", description = "loan external id in ABS")
    private String loanExtId;
    @JsonProperty("pan")
    @Schema(name = "pan", example = "860051******0293", description = "card masked pan")
    private String pan;
    @JsonProperty("amount")
    @Schema(name = "amount", example = "31240032", description = "debit amount")
    private Long amount;
    @JsonProperty("username")
    @Schema(name = "username", example = "user@example.com", description = "username")
    private String username;
    @JsonProperty("is_read")
    @Schema(name = "isRead", example = "false", description = "notification read by user or not")
    private Boolean isRead;

    public PushResponseDTO(Push push) {
        this.id = push.getId();
        this.createdTime = push.getCreatedTime();
        this.loanExtId = push.getLoanExtId();
        this.pan = push.getPan();
        this.amount = push.getAmount();
        this.username = push.getUsername();
        this.isRead = push.getIsRead();
    }
}
