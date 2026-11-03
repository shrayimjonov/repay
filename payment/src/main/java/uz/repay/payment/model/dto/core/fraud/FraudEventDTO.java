package uz.repay.payment.model.dto.core.fraud;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudEventDTO {
    private String loanExtId;
    private String cardPan;
}
