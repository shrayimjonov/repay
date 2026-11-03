package uz.repay.common.model.dto.request.card.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.StatusType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusResponseDTO {
    private Long id;
    private String code;
    private String definition;
    private String definitionUz;
    private Processing processing;
    private StatusType type;

    public StatusResponseDTO(Long statusId, String code, String definition,
                             Processing processing, StatusType type, String definitionUz) {
        this.id = statusId;
        this.code = code;
        this.definition = definition;
        this.processing = processing;
        this.type = type;
        this.definitionUz = definitionUz;
    }
}
