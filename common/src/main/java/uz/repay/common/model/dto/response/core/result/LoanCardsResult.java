package uz.repay.common.model.dto.response.core.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.params.DebitCardParams;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanCardsResult {
    private Long id;
    private String pan;
    private String holder;
    private String phone;
    private Boolean sms;
    private Long amount;
    private Integer state;
    private String stateDefinition;
    private Payer payer;
    private Processing processing;
    private DebitCardParams debitParams;

}
