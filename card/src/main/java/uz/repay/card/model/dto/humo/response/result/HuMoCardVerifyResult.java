package uz.repay.card.model.dto.humo.response.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuMoCardVerifyResult {
    @JsonProperty("message")
    private String message;
    @JsonProperty("msisdn")
    private String phone;
}
