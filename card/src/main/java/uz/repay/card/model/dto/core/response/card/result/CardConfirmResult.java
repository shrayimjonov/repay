package uz.repay.card.model.dto.core.response.card.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.core.request.card.VerifyRequestDTO;
import uz.repay.card.model.entity.Card;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardConfirmResult {
    private Card card;
    private VerifyRequestDTO verifyRequestDTO;
    private Long loanId;

}
