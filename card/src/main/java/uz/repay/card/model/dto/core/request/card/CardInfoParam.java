package uz.repay.card.model.dto.core.request.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.CardType;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardInfoParam {
    private CardType cardType;
    private List<String> cards;
}
