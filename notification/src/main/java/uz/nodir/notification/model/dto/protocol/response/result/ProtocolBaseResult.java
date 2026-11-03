package uz.nodir.notification.model.dto.protocol.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.entity.SMSProtocol;
import uz.nodir.notification.model.enums.TemplateType;
import uz.repay.common.model.enums.ResponseStatus;

import java.time.LocalDateTime;

import static uz.nodir.notification.model.enums.TemplateType.CONFIRMATION;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolBaseResult {
    @JsonProperty("id")
    @Schema(name = "id", example = "14654", description = "sms protocol id in database")
    private Long id;
    @JsonProperty("phone")
    @Schema(name = "phone", example = "998901234567", description = "phone which sent sms")
    private String phone;
    @JsonProperty("message")
    @Schema(name = "message", example = "Sizning kreditingiz yopildi!", description = "text of message")
    private String message;
    @JsonProperty("type")
    @Schema(name = "type", example = "MIB", description = "sms template type")
    private TemplateType type;
    @JsonProperty("status")
    @Schema(name = "status", example = "SUCCESS", description = "message sent response")
    private ResponseStatus status;
    @JsonProperty("loan_ext_id")
    @Schema(name = "loan_ext_id", example = "01121158076", description = "loan external id")
    private String loanExtId;
    @JsonProperty("ip_address")
    @Schema(name = "ip_address", example = "127.0.0.1", description = "sent computer ip address")
    private String ipAddress;
    @JsonProperty("createdTime")
    @Schema(name = "createdTime", example = "2024-10-16T16:14:04.592009", description = "date of sms")
    private LocalDateTime createdTime;
    @JsonProperty("created_by")
    @Schema(name = "created_by", example = "user@example.com", description = "user who sent message")
    private String createdBy;

    public ProtocolBaseResult(SMSProtocol p) {
        this.id = p.getId();
        this.phone = p.getPhone();
        if (p.getType().equals(CONFIRMATION))
            this.message = p.getMessage().replaceAll("\\d", "*");
        else
            this.message = p.getMessage();
        this.type = p.getType();
        this.status = p.getStatus();
        this.loanExtId = p.getLoanExtId();
        this.ipAddress = p.getIpAddress();
        this.createdTime = p.getInitTime();
        this.createdBy = p.getCreatedBy();
    }

}
