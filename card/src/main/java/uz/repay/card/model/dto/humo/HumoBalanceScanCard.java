package uz.repay.card.model.dto.humo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HumoBalanceScanCard {
    private Long cardId;
    private String encryptedPan;
    private String maskedPan;
    private Long updatedAtMillis;
}