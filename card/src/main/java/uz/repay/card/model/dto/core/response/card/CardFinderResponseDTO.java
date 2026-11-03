package uz.repay.card.model.dto.core.response.card;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class CardFinderResponseDTO {
    private List<String> cards;
    private int count;


    public CardFinderResponseDTO() {
        this.cards = new ArrayList<>();
        this.count = 0;
    }

    public CardFinderResponseDTO(ArrayList<String> cards) {
        this.cards = cards;
        this.count = cards.size();
    }
}
