package uz.repay.card.model.dto.core.response.card.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardFamiliarResult<R> {
    private Boolean isFamiliar;
    private String phone;
    private R result;

    public CardFamiliarResult(boolean b, String phone) {
        this.isFamiliar = b;
        this.phone = phone;
    }
}
