package uz.nodir.scoring.model.dto.limit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardLimitResponseDTO {

    @JsonProperty("pan")
    private String pan;

    @JsonProperty("description")
    private String description;

    @JsonProperty("date_to")
    private LocalDateTime dateTo;

    @JsonProperty("amount")
    private Long amount;

    @JsonProperty("used_amount")
    private Long usedAmount;
}
