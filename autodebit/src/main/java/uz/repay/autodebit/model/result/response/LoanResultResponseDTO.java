package uz.repay.autodebit.model.result.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import uz.repay.autodebit.model.result.response.result.LoanResultResult;
import uz.repay.autodebit.mongo.entity.LoanResult;
import uz.repay.common.model.dto.response.core.result.PageResult;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoanResultResponseDTO extends PageResult {
    private List<LoanResultResult> loanResults;

    public LoanResultResponseDTO(Page<LoanResult> loanResultPage) {
        super(loanResultPage.getSize(),
                loanResultPage.getNumber() + 1,
                loanResultPage.getNumberOfElements(),
                loanResultPage.getTotalPages(),
                loanResultPage.getTotalElements(),
                loanResultPage.isLast(),
                loanResultPage.isFirst());
        this.loanResults = loanResultPage
                .getContent()
                .stream()
                .map(LoanResultResult::new)
                .toList();
    }
}
