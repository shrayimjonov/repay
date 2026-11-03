package uz.repay.common.model.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum CardType {
    pan,
    token,
    account;


    public static CardType getCardType(String card) {
        log.info("Take type for: card: {}", card);

        CardType cardType;
        if (card.matches("\\d{16}"))
            cardType = CardType.pan;
        else if (card.matches("\\d{20}"))
            cardType = CardType.account;
        else
            cardType = CardType.token;

        log.info("Card type is: {}", cardType);
        return cardType;
    }
}
