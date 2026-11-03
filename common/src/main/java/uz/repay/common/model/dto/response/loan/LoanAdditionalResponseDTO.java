package uz.repay.common.model.dto.response.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.Language;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanAdditionalResponseDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("loan_ext_id")
    private String loanExtId;
    @JsonProperty("branch")
    private String branch;
    @JsonProperty("transit_balance")
    private Long transitBalance;
    @JsonProperty("transit_state")
    private Integer transitState;
    @JsonProperty("transit_account")
    private String transitAccount;
    @JsonProperty("recommended_amount")
    private Long recommendedAmount;
    @JsonProperty("debtor")
    private String debtor;
    @JsonProperty("is_insolvent")
    private Boolean isInsolvent;
    @JsonProperty("language")
    private Language language;
    @JsonProperty("currency")
    private Currency currency;
}
