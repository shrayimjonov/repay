package uz.repay.card.model.dto.huMoMiddleware.response.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuMoRealPanResult {
    @JsonProperty("pan")
    private String pan;

    @JsonProperty("bank_c")
    private String bankC;

    @JsonProperty("group_c")
    private String groupC;
}
