package uz.repay.loan.model.dto.nciBilling.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.Branch;
import uz.repay.common.model.dto.response.annotations.SimpleDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanTranRequest {
    @NotBlank
    @Pattern(regexp = "^\\d{4,}$")
    private String loanId;
    @Branch
    private String branch;
    @SimpleDate
    private String dateFrom;

    public LoanTranRequest(String loanId, String dateFrom) {
        this.loanId = loanId.substring(5);
        this.branch = loanId.substring(0, 5);
        this.dateFrom = dateFrom;
    }
}
