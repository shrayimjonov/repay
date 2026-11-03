package uz.repay.card.service.business.adapter.impl.detector;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.card.model.dto.core.request.card.CardPCCheckParam;
import uz.repay.card.model.dto.core.response.card.CardSaverResultDTO;
import uz.repay.card.model.dto.huMoMiddleware.response.result.HuMoCardsByPinflResult;
import uz.repay.card.model.dto.paidRequest.request.PaidEventDTO;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.service.business.adapter.CardDetectorService;
import uz.repay.card.service.web.HuMoClientService;
import uz.repay.card.util.ServiceUtils;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;


@Service
@Slf4j
@RequiredArgsConstructor
public class HuMoCardDetectorImpl implements CardDetectorService {
    private final HuMoClientService huMoClientService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public SimpleResponse<CardSaverResultDTO<?>> getCardsByPersonCode(Loan loan) {
        var personCode = loan.getPinfl();
        log.info("Execute card detector service: {} by personCode: {}", this, personCode);

        saveRequestEvent(loan.getExtId());

        var huMoResult = huMoClientService.getClientCardsByPINFLV2(personCode);

        if (huMoResult.getError() != null) {
            log.error("HuMo card detector method finished cause: {}", huMoResult.getError().getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), huMoResult.getError().getMessage());
        }

        if (huMoResult.getData().getError() != null) {
            log.error("HuMo card detector method finished cause: {}", huMoResult.getData().getError().getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), huMoResult.getData().getError().getMessage());
        }

        var data = huMoResult.getData().getResult();

        if (data.getData().isEmpty()) {
            var message = "Client doesn't have any cards in HuMo!";
            log.info("Finished method with reason: {}", message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        var result = new CardSaverResultDTO<>(
                loan,
                ServiceUtils.buildCardParams(
                        new CardPCCheckParam(
                                false,
                                false,
                                data
                                        .getData()
                                        .stream()
                                        .map(HuMoCardsByPinflResult.HuMoCardsByPinflData::getPan)
                                        .toList()
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

    public void saveRequestEvent(String loanExtId) {
        applicationEventPublisher
                .publishEvent(
                        new PaidEventDTO(
                                this.getClass().getSimpleName(),
                                Processing.HUMO,
                                SecurityUtils.getUsernameV2(),
                                loanExtId,
                                Payer.BORROWER
                        )
                );
    }

    @Override
    public boolean isPaid() {
        log.info("HuMo card detector is paid!!!");
        return true;
    }

    @Override
    public boolean isEnable() {
        var isEnabled = true;
        log.info("HuMo card detector service isEnabled: {}", isEnabled);
        return isEnabled;
    }

}
