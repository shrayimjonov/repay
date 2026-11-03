package uz.repay.loan.model.dto.nciBilling.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.LoanId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleLoanRequest {
    @LoanId
    private String loanId;
}
