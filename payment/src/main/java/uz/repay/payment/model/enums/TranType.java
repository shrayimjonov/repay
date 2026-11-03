package uz.repay.payment.model.enums;

public enum TranType {
    DEBIT,
    DEBIT_CONFIRM,
    DEBIT_CANCEL,
    CREDIT,
    CREDIT_REVERSE,
    SYNCHRONIZE_PC,
    CHECK,
    FOREX, // Форекс операция, обмен с одной валюты на другой
    TRANSIT, // Допольнителные транзаитные транзакции внутри банка

    FOREX_REVERSE,
    TRANSIT_REVERSE,
    SYNCHRONIZE_ABS
}
