package uz.repay.card.model.dto.core.response.card;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetCardByTokenResponse {

    String pan;
}
