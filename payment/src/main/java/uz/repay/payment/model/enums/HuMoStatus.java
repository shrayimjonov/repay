package uz.repay.payment.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum HuMoStatus {
    FINISHED(4),
    RETURNED(1),
    ACTIVE(3),
    CANCELLED(2);

    private final Integer priority;
}
