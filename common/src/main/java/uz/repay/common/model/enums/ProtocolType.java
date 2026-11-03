package uz.repay.common.model.enums;

import lombok.Getter;

@Getter
public enum ProtocolType {
    REPAY,
    INVOICE,
    INSOLVENT,
    LOAN_GUARANTOR_TOGGLE,
    CARD_LOAN_CARD_TOGGLE,
    CARD_GUARANTOR_CARD_TOGGLE

}
