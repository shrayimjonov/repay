package uz.repay.common.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Payer {
    BORROWER(1),
    CO_BORROWER(2),
    GUARANTOR(3),
    AUTO(0),
    EMPLOYEE(4);

    private final Integer priority;
}
