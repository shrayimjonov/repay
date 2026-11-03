package uz.repay.card.model.dto.core.response.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.humo.response.result.HuMoP2PInfoResult;
import uz.repay.card.model.dto.master.response.MasterP2PInfoResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.dto.uzcard.response.result.P2PInfoResult;
import uz.repay.common.utils.CardUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardShortInfoDTO {
    private String card;
    private String holder;
    private Integer status;
    private Integer pinCount;


    public CardShortInfoDTO(HuMoP2PInfoResult result) {
        this.card = CardUtils.maskPanDigits(result.getCard().getPrimaryAccountNumber());
        this.holder = result.getCard().getNameOnCard();
        this.status = Integer.valueOf(result.getCard().getStatuses().getItem().getFirst().getActionCode());
        this.pinCount = result.getCard().getPinTryCount();
    }

    public CardShortInfoDTO(P2PInfoResult result) {
        this.card = CardUtils.maskPanDigits(result.getCardNumber());
        this.holder = result.getEmbossedName();
        this.status = Integer.valueOf(result.getStatus());
        this.pinCount = result.getPinCount();
    }

    public CardShortInfoDTO(CardBaseResult cardInfo) {
        this.card = CardUtils.maskPanDigits(cardInfo.getPan());
        this.holder = cardInfo.getFullName();
        this.status = cardInfo.getStatus();
        this.pinCount = cardInfo.getPinCount();
    }

    public CardShortInfoDTO(MasterP2PInfoResponse result) {
        this.card = result.getPan();
        this.holder = result.getHolder();
        this.status = result.getStatus();
        this.pinCount = result.getPinCount();
    }
}
