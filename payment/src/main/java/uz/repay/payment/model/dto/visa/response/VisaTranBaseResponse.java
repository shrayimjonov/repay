package uz.repay.payment.model.dto.visa.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisaTranBaseResponse {
    @JsonProperty("internalNo")
    private String internalNo;
    @JsonProperty("responseCode")
    private String responseCode;
    @JsonProperty("externalId")
    private String externalId;
}
