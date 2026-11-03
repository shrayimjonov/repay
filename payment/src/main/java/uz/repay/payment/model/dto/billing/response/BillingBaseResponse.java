package uz.repay.payment.model.dto.billing.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillingBaseResponse<R> {
    private Long id;
    private String recipientShortName;
    private Object sessionId;
    private Long amount;
    private String account;
    private Integer currency;
    private String extId;
    private String requestTime;
    private String responseTime;
    private String responseText;
    private Boolean success;
    private ResponseStatus status;
    private Map<String, Object> data;
    private Object gatewayName;
    private R sender;
    private R receiver;

    public Map<Object, Object> toMap() {
        var entityMap = new HashMap<>();
        entityMap.put("id", this.id);
        entityMap.put("recipientShortName", this.recipientShortName);
        entityMap.put("sessionId", this.sessionId);
        entityMap.put("amount", this.amount);
        entityMap.put("account", this.account);
        entityMap.put("currency", this.currency);
        entityMap.put("extId", this.extId);
        entityMap.put("requestTime", this.requestTime);
        entityMap.put("responseTime", this.responseTime);
        entityMap.put("responseText", this.responseText);
        entityMap.put("success", this.success);
        entityMap.put("status", this.status);
        entityMap.put("data", this.data);
        entityMap.put("gatewayName", this.gatewayName);
        entityMap.put("sender", this.sender);
        entityMap.put("receiver", this.receiver);
        return entityMap;
    }

}
