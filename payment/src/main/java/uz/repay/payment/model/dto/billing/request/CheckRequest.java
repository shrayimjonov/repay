package uz.repay.payment.model.dto.billing.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckRequest {
    private String agentTranId;
    private String recipientId;
    private Long amount;
    private String account;
    private HashMap<String, String> params = new HashMap<>();


    public Map<Object, Object> toMap() {
        var entityMap = new HashMap<>();
        entityMap.put("agentTranId", this.agentTranId);
        entityMap.put("recipientId", this.recipientId);
        entityMap.put("amount", this.amount);
        entityMap.put("account", this.account);
        entityMap.put("params", this.params);
        return entityMap;
    }
}
