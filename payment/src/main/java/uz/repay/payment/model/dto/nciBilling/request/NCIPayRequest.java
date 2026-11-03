package uz.repay.payment.model.dto.nciBilling.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NCIPayRequest {
    @JsonProperty("loanId")
    private String loanId;
    @JsonProperty("terminalId")
    private String terminalId;
    @JsonProperty("merchantId")
    private String merchantId;
    @JsonProperty("paymentId")
    private String paymentId;
    @JsonProperty("refNum")
    private String refNum;
    @JsonProperty("amount")
    private Long amount = 0L;
    @JsonProperty("systemType")
    private String systemType;
    @JsonProperty("cardNum")
    private String cardNum;
    @JsonProperty("account")
    private String account;

    public Map<Object, Object> toMap() {
        return Map.of(
                "loanId", this.loanId,
                "terminalId", this.terminalId,
                "merchantId", this.merchantId,
                "paymentId", this.paymentId,
                "refNum", this.refNum,
                "amount", this.amount,
                "systemType", this.systemType,
                "cardNum", this.cardNum,
                "account", this.account
        );
    }
}
