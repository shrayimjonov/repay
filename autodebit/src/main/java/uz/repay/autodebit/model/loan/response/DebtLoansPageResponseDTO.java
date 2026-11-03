package uz.repay.autodebit.model.loan.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.result.PageResult;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DebtLoansPageResponseDTO extends PageResult {
    private List<DebtLoansResult> debtLoans;
}
