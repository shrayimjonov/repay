package uz.repay.card.model.dto.paidRequest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.mongo.entity.PaidRequest;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaidRequestResponseDTO {

    @JsonProperty("id")
    @Schema(name = "id", example = "d9265287-4c8e-4892-b8a3-4e3fbe838bb1", requiredMode = Schema.RequiredMode.REQUIRED, description = "pr id")
    private UUID id;

    @JsonProperty("payer")
    @Schema(name = "payer", example = "BORROWER", requiredMode = Schema.RequiredMode.REQUIRED, description = "card holder")
    private Payer payer;

    @JsonProperty("processing")
    @Schema(name = "processing", example = "DAVR_MASTER_CARD", requiredMode = Schema.RequiredMode.REQUIRED, description = "type of processing")
    private Processing processing;

    @JsonProperty("username")
    @Schema(name = "username", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED, description = "created by")
    private String username;

    @JsonProperty("created_time")
    @Schema(name = "created_time", example = "2025-02-20 09:04:39.335", requiredMode = Schema.RequiredMode.REQUIRED, description = "created time of pr")
    private LocalDateTime createdTime;

    @JsonProperty("loan_ext_id")
    @Schema(name = "loan_ext_id", example = "0107259854", requiredMode = Schema.RequiredMode.REQUIRED, description = "loan external id")
    private String loanExtId;

    public PaidRequestResponseDTO(PaidRequest paidRequest) {
        this.id = paidRequest.getId();
        this.payer = paidRequest.getPayer();
        this.processing = paidRequest.getProcessing();
        this.username = paidRequest.getUsername();
        this.createdTime = paidRequest.getCreatedTime();
        this.loanExtId = paidRequest.getLoanExtId();
    }
}
