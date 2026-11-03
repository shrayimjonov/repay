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
public class CardInfoRequestDTO {
    @Min(1)
    @NotNull
    @JsonProperty("loanId")
    @Schema(name = "loanId", example = "45", requiredMode = Schema.RequiredMode.REQUIRED, description = "id of loan")
    private Long loanId = 0L;

    @Min(1)
    @NotNull
    @JsonProperty("cardId")
    @Schema(name = "cardId", example = "22", requiredMode = Schema.RequiredMode.REQUIRED, description = "id of card")
    private Long cardId = 0L;
}
