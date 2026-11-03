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
public class HuMoGetPaymentByExtRequest {
    private String login;
    private String refNum;
    private String subSystem;

    public Map<Object, Object> toMap() {
        return Map.of(
                "login", this.login,
                "refNum", this.refNum,
                "subSystem", this.subSystem
        );
    }
}
