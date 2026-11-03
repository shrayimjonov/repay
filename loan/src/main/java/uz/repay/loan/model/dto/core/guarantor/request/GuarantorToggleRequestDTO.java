package uz.repay.loan.model.dto.core.guarantor.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuarantorToggleRequestDTO {
    @JsonProperty("loan_id")
    @Schema(name = "loan_id", description = "id of loan", requiredMode = Schema.RequiredMode.REQUIRED, example = "15")
    @Min(1)
    private Long loanId = 0L;

    @JsonProperty("guarantor_id")
    @Schema(name = "guarantor_id", description = "id of guarantor", requiredMode = Schema.RequiredMode.REQUIRED, example = "27")
    @Min(1)
    private Long guarantorId = 0L;
}
