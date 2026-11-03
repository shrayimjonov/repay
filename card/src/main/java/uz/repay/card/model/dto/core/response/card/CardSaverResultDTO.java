package uz.repay.card.model.dto.core.response.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.core.request.card.CardsParam;
import uz.repay.card.model.entity.Loan;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardSaverResultDTO<D> {
    private Loan loan;
    private List<CardsParam> param;

    /**
     * @param isChecked
     * If cards list taken from Processing method which gives client cards by person code,
     * not necessary to check card (cardholder and client person code is equals?) again inside method
     */

    private Boolean isChecked;
    private D data;
}
