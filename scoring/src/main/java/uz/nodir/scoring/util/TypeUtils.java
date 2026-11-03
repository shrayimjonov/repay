package uz.nodir.scoring.util;

import uz.nodir.scoring.model.enums.monitoring.MonitoringCardType;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.ResultState;

public class TypeUtils {
    public static SimpleResponse<MonitoringCardType> cardTypeDetector(CardType type) {
        switch (type) {
            case pan -> {
                return new SimpleResponse<>(MonitoringCardType.CARD);
            }
            case token -> {
                return new SimpleResponse<>(MonitoringCardType.TOKEN);
            }
            default -> {
                return new SimpleResponse<>(ResultState.NotProcessed.getCode(), "Card type: %s not configured".formatted(type));
            }
        }
    }
}
