package uz.nodir.scoring.model.dto.limit.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.scoring.model.entity.Limit;
import uz.repay.common.model.enums.Processing;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LimitBaseResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("description")
    private String description;

    @JsonProperty("processing")
    private Processing processing;

    public LimitBaseResponseDTO(Limit limit) {
        this.id = limit.getId();
        this.type = limit.getType();
        this.description = limit.getDescription();
        this.processing = limit.getProcessing();
    }
}
