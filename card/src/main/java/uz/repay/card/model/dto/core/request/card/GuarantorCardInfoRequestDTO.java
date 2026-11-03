package uz.repay.card.model.dto.core.request.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuarantorCardInfoRequestDTO {
    @Min(1)
    @NotNull
    @JsonProperty("guarantor_id")
    @Schema(name = "guarantor_id", example = "31", requiredMode = Schema.RequiredMode.REQUIRED, description = "id of guarantor")
    private Long guarantorId = 0L;

    @Min(1)
    @NotNull
    @JsonProperty("card_id")
    @Schema(name = "card_id", example = "22", requiredMode = Schema.RequiredMode.REQUIRED, description = "id of card")
    private Long cardId = 0L;
}
