package uz.nodir.scoring.model.dto.monitoring.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.scoring.model.enums.monitoring.MonitoringCardType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitoringBaseRequest {
    @JsonProperty("type")
    private MonitoringCardType type;

    @JsonProperty("card")
    private String card;

    @JsonProperty("dateBefore")
    @JsonFormat(pattern = "yyyyMMdd")
    private String dateBefore;

    @JsonProperty("dateAfter")
    @JsonFormat(pattern = "yyyyMMdd")
    private String dateAfter;
}
