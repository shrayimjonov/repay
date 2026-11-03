package uz.repay.card.model.dto.core.response.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Processing;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardCollectorDTO {
    private CardType cardType;
    private Processing processing;
    private String card;
}
