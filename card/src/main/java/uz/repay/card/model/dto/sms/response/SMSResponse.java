package uz.repay.card.model.dto.sms.response;


import uz.repay.common.model.enums.ResponseStatus;

public record SMSResponse(ResponseStatus status,
                          String result,
                          Long timeStamp) {
}
