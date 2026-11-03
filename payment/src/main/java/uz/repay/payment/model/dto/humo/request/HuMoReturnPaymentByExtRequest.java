package uz.repay.payment.model.dto.humo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuMoReturnPaymentByExtRequest {
    private String login;
    private String refNumber;
    private String merchantId;
    private String terminalId;
    private String subSystem;
}
