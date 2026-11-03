package uz.repay.payment.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
public enum PurposeValues {
    AMOUNT("{AMOUNT}", "Сумма"),
    RRN("{RRN}", "Ид в процессинге"),
    CARD("{CARD}", "Номер карты"),
    TERMINAL("{TERMINAL}", "Номер терминала"),
    CARD_HOLDER("{CARD_HOLDER}", "Держатель карты"),
    LOAN_ID("{LOAN_ID}", "Номер Анкеты"),
    RATE("{RATE}", "Курс валюты");

    private final String code;
    private final String description;
}
