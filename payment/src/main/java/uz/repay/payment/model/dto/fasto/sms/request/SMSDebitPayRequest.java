package uz.repay.payment.model.dto.fasto.sms.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSDebitPayRequest {
    private String phone;
    private String fullName;
    private Long amount;
    private String language;
    private String loanExtId;
    private String cardPan;
}
