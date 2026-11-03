package uz.repay.card.model.dto.core.request.guarantor;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.core.request.card.CardBaseParam;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GuarantorCardFinderRequestDTO extends CardBaseParam {
    @NotNull
    @Min(1)
    @JsonProperty("guarantor_id")
    @Schema(name = "guarantor_id", example = "12", requiredMode = Schema.RequiredMode.REQUIRED, description = "guarantor id")
    private Long guarantorId = 0L;

}
