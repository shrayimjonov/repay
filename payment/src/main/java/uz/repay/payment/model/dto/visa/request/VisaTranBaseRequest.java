package uz.repay.payment.model.dto.visa.request;

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
public class VisaTranBaseRequest {
    private String card;
    private Long amount;

    public Map<Object, Object> toMap() {
        return Map.of(
                "card", CardUtils.maskPanDigits(this.card),
                "amount", this.amount
        );
    }
}
