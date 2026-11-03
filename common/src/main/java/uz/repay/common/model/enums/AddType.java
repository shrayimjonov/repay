package uz.repay.common.model.enums;

import lombok.Getter;

@Getter
public enum AddType {
    AUTO, //auto-debit
    MANUAL, // manual by employee
    ALERTED, // paid by uzCard alert
    ALERTED_DM // card credit event from davr mobile
}
