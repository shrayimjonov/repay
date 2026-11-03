package uz.nodir.scoring.model.dto.limit.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Processing;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LimitBaseRequestDTO {

    @JsonProperty("type")
    private String type;

    @JsonProperty("description")
    private String description;

    @JsonProperty("processing")
    private Processing processing;
}
