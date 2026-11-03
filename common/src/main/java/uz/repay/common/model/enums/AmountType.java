package uz.repay.common.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum AmountType {
    COMMISSION("Комиссия"),
    TOTAL("Общий"),
    CONVERTED("Конвертированная сумма");

    private final String description;
}
