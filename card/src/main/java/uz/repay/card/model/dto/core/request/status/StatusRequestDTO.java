package uz.repay.card.model.dto.core.request.status;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.StatusType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusRequestDTO {
    @NotBlank
    private String code;
    @NotBlank
    private String definition;
    @NotBlank
    private String definitionUz;
    private Processing processing;
    private StatusType type;
}
