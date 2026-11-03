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
public class CardsListToken {
    @JsonProperty("id")
    private String id;
    @JsonProperty("pan")
    private String pan;
    @JsonProperty("mfo")
    private String mfo;
    @JsonProperty("fullName")
    private String fullName;
    @JsonProperty("contractId")
    private String contractId;
    @JsonProperty("lastActive")
    private String lastActive;
    @JsonProperty("instId")
    private String instId;
}
