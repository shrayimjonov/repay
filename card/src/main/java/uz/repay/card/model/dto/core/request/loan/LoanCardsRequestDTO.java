package uz.repay.card.model.dto.core.request.loan;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanCardsRequestDTO {
    @NotEmpty
    private List<@Min(1) @NotNull Long> loanIds;
}
