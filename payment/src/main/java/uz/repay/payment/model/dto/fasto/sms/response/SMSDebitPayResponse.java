package uz.repay.payment.model.dto.fasto.sms.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.ResponseStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSDebitPayResponse {
    private ResponseStatus status;
}
