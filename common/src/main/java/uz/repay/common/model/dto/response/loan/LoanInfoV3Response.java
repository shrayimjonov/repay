package uz.repay.common.model.dto.response.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanInfoV3Response {
    @JsonProperty("id")
    private String id;
    @JsonProperty("state")
    private Integer state;
    @JsonProperty("branch")
    private String branch;
    @JsonProperty("transit")
    private String transit;
    @JsonProperty("transit_state")
    private Integer transitState;
    @JsonProperty("transit_account")
    private String transitAccount;
    @JsonProperty("recommended_amount")
    private Long recommendedAmount;
}
