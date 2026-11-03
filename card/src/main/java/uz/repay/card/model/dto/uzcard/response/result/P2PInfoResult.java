package uz.repay.card.model.dto.uzcard.response.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class P2PInfoResult {
    @JsonProperty("CREF_NO")
    private String cardNumber;
    @JsonProperty("EMBOS_NAME")
    private String embossedName;
    @JsonProperty("EXP_DT")
    private String expiryDate;
    @JsonProperty("CARDTYPE")
    private String type;
    @JsonProperty("CARDSTATUS")
    private String status;
    @JsonProperty("CARDID")
    private String id;
    @JsonProperty("PINCNT")
    private Integer pinCount;
}
