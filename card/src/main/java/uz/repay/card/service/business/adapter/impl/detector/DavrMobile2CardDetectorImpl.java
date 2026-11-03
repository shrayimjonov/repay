package uz.repay.card.service.business.adapter.impl.detector;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.card.CardPCCheckParam;
import uz.repay.card.model.dto.core.response.card.CardSaverResultDTO;
import uz.repay.card.model.dto.dm2.response.DM2CardsByPinflResponse;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.service.business.adapter.CardDetectorService;
import uz.repay.card.service.web.DM2Service;
import uz.repay.card.util.ServiceUtils;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class DavrMobile2CardDetectorImpl implements CardDetectorService {
    private final DM2Service dm2Service;

    @Override
    public SimpleResponse<CardSaverResultDTO<?>> getCardsByPersonCode(Loan loan) {
        var personCode = loan.getPinfl();
        log.info("Execute card detector service: {} by personCode: {}", this, personCode);

        var cardFindResponse = dm2Service.getClientCardsByPINFL(personCode);

        log.info("Card finder Mobile response: {}", cardFindResponse);

        if (cardFindResponse.getError() != null) {
            log.error("Davr Mobile 2.0 card find method taken an error and will be exited");
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), cardFindResponse.getError().getMessage());
        }

        var result = cardFindResponse.getResult();


        if (result.isEmpty()) {
            String message = "Client doesn't have any cards in Davr Mobile 2.0!";
            log.warn(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        List<String> tokenList = result
                .stream()
                .map(DM2CardsByPinflResponse::getCardToken)
                .toList();

        log.info("Exit from card detector service with result: {}", tokenList);

        return new SimpleResponse<>(
                new CardSaverResultDTO<>(
                        loan,
                        ServiceUtils.buildCardParams(
                                new CardPCCheckParam(
                                        false,
                                        false,
                                        tokenList
                                )
                        ),
                        false,
                        null
                )
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
        log.info("Davr Mobile 2.0 card detector service isEnabled: {}", isEnabled);
        return isEnabled;
    }


}
