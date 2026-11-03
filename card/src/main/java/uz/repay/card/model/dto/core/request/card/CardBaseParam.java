package uz.repay.card.model.dto.core.request.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.LoanId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class CardBaseParam {

    @LoanId(message = "Loan id must be set and consist of minimum 9 digits")
    @Schema(name = "loanExtId", example = "0107259854", requiredMode = Schema.RequiredMode.REQUIRED, description = "loan external id")
    @JsonProperty("loanExtId")
    private String loanExtId;
}
