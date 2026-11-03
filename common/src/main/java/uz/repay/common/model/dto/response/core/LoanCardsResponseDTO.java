package uz.repay.common.model.dto.response.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.params.LoanCardsParam;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanCardsResponseDTO<E> {

    private List<LoanCardsParam<E>> params;
    private Integer size;

    public LoanCardsResponseDTO(List<LoanCardsParam<E>> params) {
        this.params = params;
        this.size = params.size();
    }
}
