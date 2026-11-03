package uz.repay.card.model.dto.humoGateway.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuMoBankBins {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("bankCode")
    private String bankCode;
    @JsonProperty("bankName")
    private String bankName;
    @JsonProperty("cardName")
    private String cardName;
    @JsonProperty("binCode")
    private String binCode;
    @JsonProperty("isPossible")
    private Boolean isPossible;
    @JsonProperty("centreId")
    private String centreId;
}
