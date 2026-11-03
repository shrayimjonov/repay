package uz.repay.payment.model.dto.humo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuMoReturnPaymentByIdRequest {
    private String login;
    private String paymentId;
    private String merchantId;
    private String terminalId;
    private String subSystem;

    public Map<Object, Object> toMap() {
        return Map.of(
                "login", this.login,
                "paymentId", this.paymentId,
                "merchantId", this.merchantId,
                "terminalId", this.terminalId,
                "subSystem", this.subSystem
        );
    }
}
