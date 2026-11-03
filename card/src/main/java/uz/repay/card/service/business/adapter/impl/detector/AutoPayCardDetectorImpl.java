package uz.repay.card.service.business.adapter.impl.detector;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.card.model.dto.core.request.card.CardPCCheckParam;
import uz.repay.card.model.dto.core.response.card.CardSaverResultDTO;
import uz.repay.card.model.dto.paidRequest.request.PaidEventDTO;
import uz.repay.card.model.dto.svgate.request.params.SVGateAutoPayCardsParam;
import uz.repay.card.model.dto.svgate.response.SVGateAutoPayCardsResult;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.service.business.adapter.CardDetectorService;
import uz.repay.card.service.core.EPosDAO;
import uz.repay.card.service.web.NCIBillingService;
import uz.repay.card.service.web.UzCardService;
import uz.repay.card.util.ServiceUtils;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.model.enums.ServiceType;
import uz.repay.common.utils.BranchUtils;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class AutoPayCardDetectorImpl implements CardDetectorService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final NCIBillingService nciBillingService;
    private final EPosDAO ePosDAO;
    private final UzCardService uzCardService;

    @Override
    public SimpleResponse<CardSaverResultDTO<?>> getCardsByPersonCode(Loan loan) {
        var personCode = loan.getPinfl();
        log.info("Execute card detector service: {} by personCode: {}", this, personCode);

        var loanExtId = loan.getExtId();

        var loanInfoResult = nciBillingService.getLoanInfo(loanExtId);

        if (loanInfoResult.getError() != null) {
            log.error("AutoPay card find method taken an error and will be exited");
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), loanInfoResult.getError().getMessage());
        }

        var ePosList = ePosDAO.getAll();

        var branch = BranchUtils.getBranchByLoan(loanExtId);
        var ePosOptional = ePosList
                .stream()
                .filter(z -> z.getProcessing().equals(Processing.SVGATE_AUTOPAY)
                        && z.getService().equals(ServiceType.REPAY)
                        && z.getBranch().equals(branch))
                .findFirst();

        if (ePosOptional.isEmpty()) {
            var message = "EPos not found for: %s %s".formatted(Processing.SVGATE_AUTOPAY, branch);
            log.error("AutoPay method is  finished with error: {}", message);
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), message);
        }

        var ePos = ePosOptional.get();

        var request = new SVGateAutoPayCardsParam(
                loan,
                loanInfoResult.getResult(),
                ePos
        );

        saveRequestEvent(loanExtId);

        var autoPayCardsResult = uzCardService.getAutoPayCards(request);

        if (autoPayCardsResult.getError() != null) {
            log.error("AutoPay card find method finished cause: {}", autoPayCardsResult.getError().getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), autoPayCardsResult.getError().getMessage());
        }

        if (autoPayCardsResult.getResult().getError() != null) {
            log.error("AutoPay card find method finished cause: {}", autoPayCardsResult.getResult().getError().getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), autoPayCardsResult.getResult().getError().getMessage());
        }


        List<SVGateAutoPayCardsResult> data = autoPayCardsResult.getResult().getResult();

        if (data.isEmpty()) {
            var message = "Client doesn't have any cards in AutoPay!";
            log.info("Finished method with reason: {}", message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }


        var result = new CardSaverResultDTO<>(
                loan,
                ServiceUtils.buildCardParams(
                        new CardPCCheckParam(
                                false,
                                true,
                                data.stream().map(SVGateAutoPayCardsResult::getToken).toList()
                        )
                ),
                true,
                data
        );


        log.info("Exit from card detector service with result: {}", result);
        return new SimpleResponse<>(
                result
        );
    }

    private ResultData<UzCardBaseResponse<List<SVGateAutoPayCardsResult>>> getAutoPayResult(SVGateAutoPayCardsParam request) {
        int attempt = 0;
        int maxAttempts = 3;
        ResultData<UzCardBaseResponse<List<SVGateAutoPayCardsResult>>> resultResponse;
        do {
            log.info("Send {} request to take autoPay cards. Max attempt is: {}", attempt, maxAttempts);
            attempt++;
            resultResponse = uzCardService.getAutoPayCards(request);

            if (resultResponse.getResult() != null)
                return resultResponse;

            if (!resultResponse.getError().getCode().equals(ResultState.TimeOutError.getCode()))
                return resultResponse;


        } while (attempt < maxAttempts);

        return resultResponse;
    }

    @Override
    public boolean isPaid() {
        log.info("AutoPay card detector is paid!!!");
        return true;
    }


    public void saveRequestEvent(String loanExtId) {
        applicationEventPublisher
                .publishEvent(
                        new PaidEventDTO(
                                this.getClass().getSimpleName(),
                                Processing.SVGATE_AUTOPAY,
                                SecurityUtils.getUsernameV2(),
                                loanExtId,
                                Payer.BORROWER
                        )
                );
    }

    @Override
    public boolean isEnable() {
        var isEnabled = true;
        log.info("AutoPay card detector service isEnabled: {}", isEnabled);
        return isEnabled;
    }


}
