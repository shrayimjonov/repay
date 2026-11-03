package uz.repay.card.model.dto.humo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HumoBalanceDetectedEvent {
    private String eventId;
    private Long cardId;
    private String encryptedPan;
    private String maskedPan;
    private Long detectedAtMillis;
    private Long availableAmount;
}