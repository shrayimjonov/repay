package uz.repay.payment.model.dto.nciBilling.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NCIForexTranRequest {
    private String branch;
    private String accountDebit;
    private String accountCredit;
    private Long amount;
    private String currency;
    private Long extId;
    private String purpose;

    public Map<Object, Object> toMap() {
        return Map.of(
                "branch", this.branch,
                "accountDebit", this.accountDebit,
                "accountCredit", this.accountCredit,
                "amount", this.amount,
                "currency", this.currency,
                "extId", this.extId,
                "purpose", this.purpose
        );
    }
}
