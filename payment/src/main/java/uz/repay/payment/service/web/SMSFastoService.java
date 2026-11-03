package uz.repay.payment.service.web;

import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.model.dto.fasto.sms.request.SMSDebitPayRequest;
import uz.repay.payment.model.dto.fasto.sms.response.SMSDebitPayResponse;

public interface SMSFastoService {
    SimpleResponse<SMSDebitPayResponse> sendDebitMessage(SMSDebitPayRequest request);
}
