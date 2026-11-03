package uz.repay.card.model.dto.adapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.entity.base.BaseCardEntity;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardBalanceFetchRequest {
    private Long cardId;
    private Processing processing;
    private CardType type;
    private String card;
    private Payer payer;
    private BaseCardEntity baseCard;


    public CardBalanceFetchRequest(BaseCardEntity baseCard, CardType cardType, String cardAccount) {
        this.baseCard = baseCard;
        this.cardId = baseCard.getCard().getId();
        this.processing = baseCard.getCard().getProcessing();
        this.type = cardType;
        this.card = cardAccount;
        this.payer = baseCard.getPayer();
    }

}
