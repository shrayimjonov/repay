package uz.repay.common.model.dto.response.core.params;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanCardsParam<E> {
    private Long loanId;
    private List<E> loanCardsResults;

    public LoanCardsParam(Long loanId, ResultData<List<E>> listResultData) {
        this.loanId = loanId;
        this.loanCardsResults = listResultData.getResult();
    }
}
