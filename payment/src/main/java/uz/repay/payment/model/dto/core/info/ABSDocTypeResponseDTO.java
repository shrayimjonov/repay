package uz.repay.payment.model.dto.core.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.payment.model.enums.ABSDocType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ABSDocTypeResponseDTO {
    private ABSDocType absDocType;
    private String description;

    public ABSDocTypeResponseDTO(ABSDocType absDocType) {
        this.absDocType = absDocType;
        this.description = absDocType.getDescription();
    }
}
