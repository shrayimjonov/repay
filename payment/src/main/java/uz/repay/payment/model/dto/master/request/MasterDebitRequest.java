package uz.repay.payment.model.dto.master.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterDebitRequest {
    @JsonProperty("amount")
    private Long amount;
    @JsonProperty("merchantId")
    private String merchantId;
    @JsonProperty("terminalId")
    private String terminalId;
    @JsonProperty("token")
    private String token;
    @JsonProperty("extId")
    private String extId;
    @JsonProperty("type")
    private String type;
    @JsonProperty("commission")
    private Long commission;
    @JsonProperty("operation")
    private String operation;

    public Map<Object, Object> toMap() {
        return Map.of(
                "amount", this.amount,
                "merchantId", this.merchantId,
                "terminalId", this.terminalId,
                "token", this.token,
                "extId", this.extId,
                "type", this.type,
                "commission", this.commission,
                "operation", this.operation
        );
    }
}
