package uz.repay.card.model.dto.humo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HumoCardsByPINFLRequest {
    private String personCode;
}
