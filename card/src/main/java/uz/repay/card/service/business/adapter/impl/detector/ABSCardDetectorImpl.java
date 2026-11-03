package uz.repay.card.service.business.adapter.impl.detector;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.card.CardPCCheckParam;
import uz.repay.card.model.dto.core.response.card.CardSaverResultDTO;
import uz.repay.card.model.dto.nciBilling.response.NCICardsByPersonCodeResponse;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.service.business.adapter.CardDetectorService;
import uz.repay.card.service.web.NCIBillingService;
import uz.repay.card.util.ServiceUtils;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;


@Service
@Slf4j
@RequiredArgsConstructor
public class ABSCardDetectorImpl implements CardDetectorService {
    private final NCIBillingService nciBillingService;

    @Override
    public SimpleResponse<CardSaverResultDTO<?>> getCardsByPersonCode(Loan loan) {
        var personCode = loan.getPinfl();
        log.info("Execute card detector service: {} by personCode: {}", this, personCode);

        var cardFindResponse = nciBillingService.getCardsByPersonCode(personCode);

        if (cardFindResponse.getError() != null) {
            log.error("ABS card find method taken an error and will be exited");
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), cardFindResponse.getError().getMessage());
        }

        var data = cardFindResponse.getResult();

        if (data.isEmpty()) {
            String message = "Client doesn't have any cards in ABS!";
            log.warn(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        var detectedCardsResult =
                data
                        .stream()
                        .map(NCICardsByPersonCodeResponse::getPan)
                        .toList();

        var result = new CardSaverResultDTO<>(
                loan,
                ServiceUtils.buildCardParams(
                        new CardPCCheckParam(
                                false,
                                false,
                                detectedCardsResult
                        )
                ),
                false,
                null
        );

        log.info("Exit from card detector service with result: {}", result);
        return new SimpleResponse<>(
                result
        );

    }

    @Override
    public boolean isPaid() {
        log.info("Davr mobile 2.0 card detector is free method");
        return false;
    }

    @Override
    public boolean isEnable() {
        var isEnabled = true;
        log.info("ABS card detector service isEnabled: {}", isEnabled);
        return isEnabled;
    }


}
