package uz.repay.card.service.business.component;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.repay.card.model.dto.core.request.autopay.request.AutoPayPushRequestDTO;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.service.business.CardBalanceAlertService;
import uz.repay.common.model.dto.response.core.CoreError;

@Component
@Slf4j
@RequiredArgsConstructor
public class AutoPayPushComponent {
    private final CardBalanceAlertService cardBalanceAlertService;

    public UzCardBaseResponse<CoreError> pushBalance(@NotNull AutoPayPushRequestDTO requestDTO) {
        log.info("RECEIVED ALERT CARD : {}, {}", requestDTO.getCardId(), requestDTO.getPan());
        cardBalanceAlertService.pushToLoansWithAlertedCard(requestDTO);

        var response = new UzCardBaseResponse<>(
                "2.0",
                System.currentTimeMillis(),
                new CoreError(0, "Successfully received!")
        );

        log.info("Finished command to push card balance with response: {}", response);
        return response;
    }
}
