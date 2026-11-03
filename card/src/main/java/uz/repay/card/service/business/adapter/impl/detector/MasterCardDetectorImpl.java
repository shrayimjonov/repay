package uz.repay.card.service.business.adapter.impl.detector;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.card.CardPCCheckParam;
import uz.repay.card.model.dto.core.response.card.CardSaverResultDTO;
import uz.repay.card.model.dto.master.request.MasterCustomerCardsByPINFLRequest;
import uz.repay.card.model.dto.master.response.MasterCustomerCardsResponse;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.service.business.adapter.CardDetectorService;
import uz.repay.card.service.web.MasterClientService;
import uz.repay.card.util.ServiceUtils;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CardUtils;


@Service
@Slf4j
@RequiredArgsConstructor
public class MasterCardDetectorImpl implements CardDetectorService {
    private final MasterClientService masterClientService;

    @Override
    public SimpleResponse<CardSaverResultDTO<?>> getCardsByPersonCode(Loan loan) {
        var personCode = loan.getPinfl();
        log.info("Execute card detector service: {} by personCode: {}", this, personCode);

        var request = new MasterCustomerCardsByPINFLRequest(
                personCode
        );

        var cardFindResponse = masterClientService.customerCardsByPersonCode(request);

        if (!cardFindResponse.getStatus()) {
            log.error("Master card find method taken an error and will be exited");
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), cardFindResponse.getError().getMessage());
        }

        if (cardFindResponse.getData().getError() != null) {
            log.error("BPC connect result is un success: {}", cardFindResponse.getData().getError().getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), cardFindResponse.getData().getError().getMessage());
        }

        var data = cardFindResponse.getData().getResult();

        if (data.isEmpty()) {
            String message = "Client doesn't have any cards from MASTER BPC!";
            log.warn(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        data
                .removeIf(
                        z -> !z.getToken().matches(CardUtils.regexDavrMasterCardToken())
                );

        var detectedCardsResult =
                data
                        .stream()
                        .map(MasterCustomerCardsResponse::getToken)
                        .toList();


        var result = new CardSaverResultDTO<>(
                loan,
                ServiceUtils.buildCardParams(
                        new CardPCCheckParam(
                                true,
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
        log.info("Master card detector is free method");
        return false;
    }

    @Override
    public boolean isEnable() {
        var isEnabled = true;
        log.info("Master card detector service isEnabled: {}", isEnabled);
        return isEnabled;
    }


}
