package uz.repay.loan.model.dto.core.loan.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.LoanId;
import uz.repay.common.model.dto.response.annotations.SimpleDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanABSTranRequestDTO {
    @LoanId
    private String loanExtId;
    @SimpleDate
    private String dateFrom;
}
