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
public class OTPResult {
    @JsonProperty("id")
    private String id;
    @JsonProperty("phoneMask")
    private String phoneMask;
    @JsonProperty("token")
    private String token;
    @JsonProperty("verified")
    private Boolean verified;
}
