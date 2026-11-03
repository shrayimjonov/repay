package uz.repay.common.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultState {
    IncorrectProcessing("This type processing not serviced", 1021),
    PhoneNotFound("Phone number of card not taken or not turned on", 1022),
    SMSNotSent("Problem with sms center", 1023),
    CardNotFound("Card not found!", 1024),
    DataNotFound("Data not found!", 1025),
    EntityNotFound("Entity not found!", 1026),
    InternalServerError("In Back something went wrong", 1027),
    SQLException("Database process error", 1028),
    WrongParams("Field or value filled incorrectly!", 1029),
    ThirdPartyError("Third Party connection error...", 1030),
    InvalidRequest("Error caused by incorrect parameters", 1031),
    TimeOutError("Connection time out", 1032),
    NotProcessed("The operation was not processed to completion.", 1033),
    BlockOperation("This operation is blocked", 1034),
    NOT_IMPLEMENTED("Method or interface not implemented", 1035),
    LOAN_DEBT_CLOSED("Recommended amount is 0", 1036),
    INSUFFICIENT_FUNDS("Insufficient funds on card", 1037),
    LIMIT_REACHED("Attempts limit exhausted", 1038),
    INCORRECT_PHONE("Incorrect phone number!", 1039),
    ;
    private final String message;
    private final Integer code;
}
