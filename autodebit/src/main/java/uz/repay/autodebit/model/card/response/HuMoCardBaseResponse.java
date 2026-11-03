package uz.repay.autodebit.model.card.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.CoreError;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuMoCardBaseResponse<R> {
    @JsonProperty("id")
    private String id;
    @JsonProperty("result")
    private R result;
    @JsonProperty("error")
    private CoreError error;
}
