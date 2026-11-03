package uz.repay.card.model.dto.core.request.loan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.LoanId;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuarantorCardToggleRequestDTO {
    @Min(1)
    @NotNull
    @JsonProperty("guarantor_id")
    @Schema(name = "guarantor_id", example = "45", requiredMode = Schema.RequiredMode.REQUIRED, description = "id of loan")
    private Long guarantorId = 0L;

    @Min(1)
    @NotNull
    @JsonProperty("card_id")
    @Schema(name = "card_id", example = "22", requiredMode = Schema.RequiredMode.REQUIRED, description = "id of card")
    private Long cardId = 0L;

    @LoanId
    @JsonProperty("loan_external_id")
    @Schema(name = "loan_external_id", example = "0107259854", requiredMode = Schema.RequiredMode.REQUIRED, description = "loan external id")
    private String loanExternalId;

    public Map<Object, Object> toMap() {
        return Map.of(
                "guarantor_id", this.guarantorId,
                "card_id", this.cardId,
                "loan_external_id", this.loanExternalId
        );
    }
}
