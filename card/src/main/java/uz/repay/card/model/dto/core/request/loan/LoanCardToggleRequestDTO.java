package uz.repay.card.model.dto.core.request.loan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanCardToggleRequestDTO {
    @Min(1)
    @NotNull
    @JsonProperty("loan_id")
    @Schema(name = "loan_id", example = "45", requiredMode = Schema.RequiredMode.REQUIRED, description = "id of loan")
    private Long loanId = 0L;

    @Min(1)
    @NotNull
    @JsonProperty("card_id")
    @Schema(name = "card_id", example = "22", requiredMode = Schema.RequiredMode.REQUIRED, description = "id of card")
    private Long cardId = 0L;

    public Map<Object, Object> toMap() {
        return Map.of(
                "loan_id", this.loanId,
                "card_id", this.cardId
        );
    }
}
