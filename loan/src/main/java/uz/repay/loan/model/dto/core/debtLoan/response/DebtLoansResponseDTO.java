package uz.repay.loan.model.dto.core.debtLoan.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import uz.repay.common.model.dto.response.core.result.PageResult;
import uz.repay.loan.model.dto.core.debtLoan.response.result.DebtLoansResult;
import uz.repay.loan.model.entity.DebtLoan;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DebtLoansResponseDTO extends PageResult {
    private List<DebtLoansResult> visits;

    public DebtLoansResponseDTO(Page<DebtLoan> debtLoanPage) {
        super(debtLoanPage.getSize(),
                debtLoanPage.getNumber() + 1,
                debtLoanPage.getNumberOfElements(),
                debtLoanPage.getTotalPages(),
                debtLoanPage.getTotalElements(),
                debtLoanPage.isLast(),
                debtLoanPage.isFirst());
        this.visits = debtLoanPage
                .getContent()
                .stream()
                .map(DebtLoansResult::new)
                .toList();
    }
}
