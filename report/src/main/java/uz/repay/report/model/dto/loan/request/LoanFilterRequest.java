package uz.repay.report.model.dto.loan.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.loan.params.LoanSearchParams;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanFilterRequest {
    private LoanSearchParams params;
}
