package uz.repay.payment.model.dto.humo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuMoCancelPaymentByIdRequest {
    private String login;
    private String paymentId;
    private String subSystem;

    public Map<Object, Object> toMap() {
        return Map.of(
                "login", this.login,
                "paymentId", this.paymentId,
                "subSystem", this.subSystem
        );
    }
}
