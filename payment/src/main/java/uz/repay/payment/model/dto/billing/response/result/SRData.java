package uz.repay.payment.model.dto.billing.response.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SRData {
    @JsonProperty("ACCOUNT")
    private String account;
    @JsonProperty("EXT_ID")
    private String externalId;
}
