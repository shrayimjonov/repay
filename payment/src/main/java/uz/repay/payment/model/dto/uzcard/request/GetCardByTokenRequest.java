package uz.repay.payment.model.dto.uzcard.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCardByTokenRequest {
    String token;
}
