package uz.repay.payment.model.dto.core.fraud;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudClearListDTO {

    private List<String> loanExternalIds;
}
