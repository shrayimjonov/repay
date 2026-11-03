package uz.repay.payment.model.dto.humo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.utils.CardUtils;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HuMoDebitRequest {
    private String refNum;
    private String pan;
    private String expiry;
    private Long amount;
    private String merchantId;
    private String terminalId;
    private String login;
    private String subSystem;

    public Map<Object, Object> toMap() {
        return Map.of(
                "refNum", this.refNum,
                "pan", CardUtils.maskPanDigits(this.pan),
                "expiry", this.expiry,
                "amount", this.amount,
                "merchantId", this.merchantId,
                "terminalId", this.terminalId,
                "login", this.login,
                "subSystem", this.subSystem
        );
    }
}
