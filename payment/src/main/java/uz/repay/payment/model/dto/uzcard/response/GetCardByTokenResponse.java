package uz.repay.payment.model.dto.uzcard.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetCardByTokenResponse {

    String pan;
}
