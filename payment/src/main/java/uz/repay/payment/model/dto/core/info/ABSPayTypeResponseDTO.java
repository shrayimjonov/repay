package uz.repay.payment.model.dto.core.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.payment.model.enums.ABSPayType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ABSPayTypeResponseDTO {
    private ABSPayType absPayType;
    private String description;

    public ABSPayTypeResponseDTO(ABSPayType absPayType) {
        this.absPayType = absPayType;
        this.description = absPayType.getDescription();
    }
}
