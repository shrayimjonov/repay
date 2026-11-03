package uz.repay.payment.model.dto.billing.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelRequest {
    private String billId;

    public Map<Object, Object> toMap() {
        return Map.of("billId", this.billId);
    }
}
