package uz.repay.card.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum LoanType {
    AUTO_CREDIT(1, "Автокредит"),
    MORTGAGE(2, "Ипотека"),
    MICROLOAN(3, "Микрозайм"),
    EDUCATION(4, "Образовательный кредит"),
    CONSUMER(5, "Потребительский кредит");

    private final Integer code;
    private final String description;

}
