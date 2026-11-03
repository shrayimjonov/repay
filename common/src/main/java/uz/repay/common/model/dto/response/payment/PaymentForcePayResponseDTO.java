package uz.repay.common.model.dto.response.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.params.DebitCardParams;
import uz.repay.common.model.enums.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentForcePayResponseDTO {
    @JsonProperty("remainingAmount")
    private Long remainingAmount;
    @JsonProperty("loanExtId")
    private String loanExtId;
    @JsonProperty("currency")
    private Currency currency;
    @JsonProperty("debitCardParams")
    private DebitCardParams debitCardParams;

}
