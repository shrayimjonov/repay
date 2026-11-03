package uz.repay.card.model.dto.huMoMiddleware.request.params;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuMoCheckCardToPINFLParam {
    @JsonProperty("person_code")
    private String personCode;
    @JsonProperty("pan")
    private String pan;
}
