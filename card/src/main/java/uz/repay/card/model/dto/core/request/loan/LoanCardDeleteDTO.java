package uz.repay.card.model.dto.core.request.loan;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanCardDeleteDTO {
    @Min(1)
    private Long loanId = 0L;
    @Min(1)
    private Long cardId = 0L;
}
