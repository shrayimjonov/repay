package uz.repay.card.model.dto.paidRequest.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaidEventDTO {
    private String beanName;
    private Processing processing;
    private String username;
    private String loanExtId;
    private Payer payer;

}
