package uz.repay.payment.model.dto.core.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyResponseDTO {
    private Currency currency;
    private Integer code;

    public CurrencyResponseDTO(Currency currency) {
        this.currency = currency;
        this.code = currency.getCode();
    }
}
