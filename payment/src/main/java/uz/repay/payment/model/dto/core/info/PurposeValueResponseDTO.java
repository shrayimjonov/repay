package uz.repay.payment.model.dto.core.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.payment.model.enums.PurposeValues;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurposeValueResponseDTO {
    private PurposeValues value;
    private String code;
    private String description;

    public PurposeValueResponseDTO(PurposeValues purposeValue) {
        this.value = purposeValue;
        this.code = purposeValue.getCode();
        this.description = purposeValue.getDescription();
    }
}
