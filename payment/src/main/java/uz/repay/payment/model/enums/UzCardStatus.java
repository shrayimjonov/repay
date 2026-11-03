package uz.repay.payment.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum UzCardStatus {
    OK(4),
    ROK(1),
    ERR(3),
    RER(2);

    private final Integer priority;
}
