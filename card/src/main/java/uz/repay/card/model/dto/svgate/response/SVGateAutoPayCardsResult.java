package uz.repay.card.model.dto.svgate.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SVGateAutoPayCardsResult {
    @JsonProperty("id")
    private String token;
    @JsonProperty("pan")
    private String pan;
    @JsonProperty("fullName")
    private String fullName;
    @JsonProperty("phone")
    private String phone;
}
