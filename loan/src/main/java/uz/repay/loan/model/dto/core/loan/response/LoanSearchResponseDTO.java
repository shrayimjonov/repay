package uz.repay.loan.model.dto.core.loan.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import uz.repay.common.model.dto.response.core.result.PageResult;
import uz.repay.loan.model.entity.Loan;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoanSearchResponseDTO extends PageResult {
    private List<LoanShortResponseDTO> loans;

    public LoanSearchResponseDTO(Page<Loan> loanPageList) {
        super(loanPageList.getSize(),
                loanPageList.getNumber() + 1,
                loanPageList.getNumberOfElements(),
                loanPageList.getTotalPages(),
                loanPageList.getTotalElements(),
                loanPageList.isLast(),
                loanPageList.isFirst());
        this.loans = loanPageList
                .getContent()
                .stream().map(LoanShortResponseDTO::new)
                .toList();
    }
}
