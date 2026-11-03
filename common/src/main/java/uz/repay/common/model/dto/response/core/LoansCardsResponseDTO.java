package uz.repay.common.model.dto.response.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoansCardsResponseDTO<E> {

    private String loanExtId;
    private List<E> cards = new ArrayList<>();


    public LoansCardsResponseDTO(String extId, E cc) {
        this.loanExtId = extId;
        this.cards.add(cc);
    }
}
