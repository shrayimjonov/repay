package uz.repay.card.model.dto.core.request.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardPCCheckParam {
    private boolean isMasterCard;
    private boolean isAutoPayCard;
    private List<String> cards;
}
