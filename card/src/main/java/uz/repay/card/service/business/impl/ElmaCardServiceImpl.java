package uz.repay.card.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.elma.request.ElmaVisaCardFindRequest;
import uz.repay.card.model.dto.elma.response.ElmaVisaCardFindResponse;
import uz.repay.card.service.business.ElmaCardService;
import uz.repay.card.service.web.ElmaClientService;
import uz.repay.card.service.web.NCIBillingService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ElmaCardServiceImpl implements ElmaCardService {
    private final ElmaClientService elmaClientService;
    private final NCIBillingService nciBillingService;

    @Override
    public SimpleResponse<List<ElmaVisaCardFindResponse.ElmaVisaCardResult>> findClientVisaCardsByCodes(String pinfl) {
        log.info("Start find client visa cards by person_code: {}", pinfl);

        var clientResult = nciBillingService.getClientInfoByPersonCode(pinfl);

        if (!clientResult.getStatus()) {
            log.error("NCI Billing result can't taken... Cause: {}", clientResult.getError());
            return new SimpleResponse<>(clientResult.getError().getCode(), clientResult.getError().getMessage());
        }

        if (clientResult.getData().getError() != null) {
            log.error("NCI Billing result unsuccessful...");
            return new SimpleResponse<>(clientResult.getData().getError().getCode(), clientResult.getData().getError().getMessage());
        }

        if (clientResult.getData().getResult().isEmpty()) {
            var message = "Not found any client...";
            log.error(message);
            return new SimpleResponse<>(ResultState.DataNotFound.getCode(), message);
        }


        var request = new ElmaVisaCardFindRequest()
                .builder(clientResult.getData().getResult());

        var elmaResult = elmaClientService.getClientCardsByPINFL(request);

        if (!elmaResult.getStatus()) {
            log.error("Elma result can't taken... Cause: {}", elmaResult.getError());
            return new SimpleResponse<>(elmaResult.getError().getCode(), elmaResult.getError().getMessage());
        }

        if (elmaResult.getData().getRes() != 0 || elmaResult.getData().getCards() == null) {
            var message = "Elma error message: %s".formatted(elmaResult.getData().getErr());
            log.error(message);
            return new SimpleResponse<>(ResultState.NotProcessed.getCode(), message);
        }

        return new SimpleResponse<>(elmaResult.getData().getCards());
    }
}
