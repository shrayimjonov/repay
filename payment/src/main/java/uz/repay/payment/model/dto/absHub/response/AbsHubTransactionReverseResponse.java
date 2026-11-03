package uz.repay.payment.model.dto.absHub.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbsHubTransactionReverseResponse {
    @JsonProperty("generalId")
    private String generalId;
    @JsonProperty("amountInAccountBalance")
    private Long amountInAccountBalance;
}
