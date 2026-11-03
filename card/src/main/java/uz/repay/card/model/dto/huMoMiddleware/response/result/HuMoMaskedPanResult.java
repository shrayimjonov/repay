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
public class HuMoMaskedPanResult {
    @JsonProperty("card")
    private String card;
    @JsonProperty("bank_c")
    private String bank_c;
    @JsonProperty("group_c")
    private String group_c;
}
