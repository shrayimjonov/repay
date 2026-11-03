package uz.repay.loan.model.dto.core.loan.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.LoanId;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanRepayRequestDTO {
    @LoanId
    private String loanExternal;
    @Min(1)
    private Long amount = 0L;

    public Map<Object, Object> toMap() {
        return Map.of(
                "loanExternal", this.loanExternal,
                "amount", this.amount
        );
    }
}
