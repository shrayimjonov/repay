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
public class NCISimpleTranRequest {
    private String accountDebit;
    private String branchDebit;
    private String accountCredit;
    private String branchCredit;
    private Long amount;
    private String purpose;
    private Long extId;

    public Map<Object, Object> toMap() {
        return Map.of(
                "accountDebit", this.accountDebit,
                "branchDebit", this.branchDebit,
                "accountCredit", this.accountCredit,
                "branchCredit", this.branchCredit,
                "amount", this.amount,
                "purpose", this.purpose,
                "extId", this.extId
        );
    }
}
