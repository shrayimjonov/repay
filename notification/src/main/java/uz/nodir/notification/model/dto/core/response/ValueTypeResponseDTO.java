package uz.nodir.notification.model.dto.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.enums.ValueType;

@Data
@NoArgsConstructor
public class ValueTypeResponseDTO {
    private ValueType valueType;

    public ValueTypeResponseDTO(ValueType valueType) {
        this.valueType = valueType;
    }
}
