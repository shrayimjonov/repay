package uz.repay.card.model.dto.core.request.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.entity.Loan;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardSaverParamDTO<D> {
    private Loan loan;
    private List<String> cardList;

    /**
     * @param isChecked
     * If cards list taken from Processing method which gives client cards by person code,
     * not necessary to check card (cardholder and client person code are equals?) again inside method
     */

    private Boolean isChecked;
    private D data;
}
