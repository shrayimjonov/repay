package uz.repay.card.service.business.adapter.impl.detector;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.card.CardPCCheckParam;
import uz.repay.card.model.dto.core.response.card.CardSaverResultDTO;
import uz.repay.card.model.dto.elma.response.ElmaVisaCardFindResponse;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.service.business.ElmaCardService;
import uz.repay.card.service.business.adapter.CardDetectorService;
import uz.repay.card.util.ServiceUtils;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CardUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class VisaCardDetectorImpl implements CardDetectorService {
    private final ElmaCardService elmaCardService;

    @Override
    public SimpleResponse<CardSaverResultDTO<?>> getCardsByPersonCode(Loan loan) {
        var personCode = loan.getPinfl();
        log.info("Execute card detector service: {} by personCode: {}", this, personCode);

        var elmaResult = elmaCardService.findClientVisaCardsByCodes(personCode);
        if (!elmaResult.getStatus()) {
            log.error("Visa card find method taken an error and will be exited");
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), elmaResult.getError().getMessage());
        }


        var data = new ArrayList<>(elmaResult.getData());

        data
                .removeIf(
                        z -> !z.getCardNum().matches(CardUtils.regexVisaDAVRToken())
                        || !z.getCardStatus().equals("0")
                );

        if (data.isEmpty()) {
            String message = "Client doesn't have any cards from Visa!";
            log.warn(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        var detectedCardsResult =
                data
                        .stream()
                        .map(ElmaVisaCardFindResponse.ElmaVisaCardResult::getCardNum)
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
                true,
                null
        );

        log.info("Exit from card detector service with result: {}", result);
        return new SimpleResponse<>(
                result
        );
    }

    @Override
    public boolean isPaid() {
        log.info("Visa card detector is free method");
        return false;
    }

    @Override
    public boolean isEnable() {
        var isEnabled = true;
        log.info("Visa card detector service isEnabled: {}", isEnabled);
        return isEnabled;
    }

}
