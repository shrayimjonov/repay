package uz.repay.payment.model.dto.core.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.AmountType;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmountTypeResponseDTO {
    private AmountType type;
    private String description;

    public AmountTypeResponseDTO(AmountType type) {
        this.type = type;
        this.description = type.getDescription();
    }
}
