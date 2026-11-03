package uz.repay.card.model.dto.uzcard.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckCard2PinflRequest {
    @JsonProperty("token")
    private String token;
    @JsonProperty("personCode")
    private String personCode;
}
