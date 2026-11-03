package uz.nodir.scoring.model.dto.monitoring.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.enums.ResponseStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitoringBaseResponse<D> {
    @JsonProperty("status")
    private ResponseStatus status;
    @JsonProperty("success")
    private boolean success;
    @JsonProperty("data")
    private D data;
    @JsonProperty("error")
    private CoreError error;
}
