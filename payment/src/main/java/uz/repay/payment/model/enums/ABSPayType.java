package uz.repay.payment.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
public enum ABSPayType {
    FOREX_RESIDENT("конверсия для резидентов"),
    FOREX_NON_RESIDENT("конверсия для не-резидентов"),
    INTER_BRANCH("меж филиал"),
    SIMPLE_PAY("обычная проводка");

    private final String description;
}
