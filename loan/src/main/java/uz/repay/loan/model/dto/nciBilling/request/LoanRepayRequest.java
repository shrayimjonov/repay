package uz.repay.loan.model.dto.nciBilling.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanRepayRequest {
    @JsonProperty("loanExternal")
    private String loanExternal;
    @JsonProperty("amount")
    private Long amount;
}
