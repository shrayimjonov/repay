package uz.repay.card.model.dto.core.response.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import uz.repay.card.model.entity.Card;
import uz.repay.common.model.enums.Processing;

@Data
@AllArgsConstructor
public class VerifyResponseDTO {
    private Long id;
    private String pan;
    private String cardHolder;
    private Processing type;

    public VerifyResponseDTO(Card card) {
        this.id = card.getId();
        this.pan = card.getMaskedPan();
        this.cardHolder = card.getHolder();
        this.type = card.getProcessing();
    }
}
