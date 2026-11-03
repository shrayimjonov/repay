package uz.repay.autodebit.model.card.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LoanCardsRequestDTO {
    private List<String> loanIds;

    public LoanCardsRequestDTO(final List<String> loanIds) {
        this.loanIds = loanIds;
    }
}
