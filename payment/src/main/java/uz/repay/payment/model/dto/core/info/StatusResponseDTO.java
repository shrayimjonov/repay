package uz.repay.payment.model.dto.core.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.ResponseStatus;

@Data
@NoArgsConstructor
public class StatusResponseDTO {
    private ResponseStatus status;

    public StatusResponseDTO(ResponseStatus status) {
        this.status = status;
    }
}
