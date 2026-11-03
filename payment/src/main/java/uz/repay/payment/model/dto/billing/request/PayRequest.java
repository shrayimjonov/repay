package uz.repay.payment.model.dto.billing.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.payment.model.enums.PayType;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayRequest {
    private String billId;
    private PayType type;
    private Map<String, String> params = new HashMap<>();

    public Map<Object, Object> toMap() {
        var entityMap = new HashMap<>();
        entityMap.put("billId", this.billId);
        entityMap.put("type", this.type);
        entityMap.put("params", this.params);
        return entityMap;
    }
}
