package uz.nodir.scoring.model.dto.uzCard.response.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardLimitResult {

    @JsonProperty("limitId")
    private Integer limitId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("limitAmount")
    private Long limitAmount;

    @JsonProperty("usedAmount")
    private Long usedAmount;

    @JsonProperty("cycleType")
    private Integer cycleType;
}
