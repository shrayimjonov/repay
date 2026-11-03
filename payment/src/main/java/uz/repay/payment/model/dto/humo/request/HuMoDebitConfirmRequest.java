package uz.repay.payment.model.dto.humo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HuMoDebitConfirmRequest {
    private String paymentId;
    private String login;
    private String subSystem;

    public Map<Object, Object> toMap() {
        return Map.of(
                "paymentId", this.paymentId,
                "login", this.login,
                "subSystem", this.subSystem
        );
    }
}
