package uz.nodir.scoring.service.business.main.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.scoring.model.dto.history.request.CardHistoryRequestDTO;
import uz.nodir.scoring.model.dto.history.response.CardHistoryResponseDTO;
import uz.nodir.scoring.model.dto.monitoring.request.MonitoringBaseRequest;
import uz.nodir.scoring.model.enums.monitoring.MonitoringCardType;
import uz.nodir.scoring.service.business.main.MonitoringService;
import uz.nodir.scoring.service.web.MonitoringClientService;
import uz.nodir.scoring.util.TypeUtils;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CardUtils;
import uz.repay.common.utils.DateUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MonitoringServiceImpl implements MonitoringService {
    private final MonitoringClientService monitoringClientService;

    @Override
    public ResultData<List<CardHistoryResponseDTO>> takeCardHistory(@NotNull CardHistoryRequestDTO requestDTO) {
        log.info("Received command to take card history with request: {}", requestDTO);


        var cardTypeResult = TypeUtils.cardTypeDetector(requestDTO.getParams().getType());

        if (!cardTypeResult.getStatus()) {
            log.error("Card history method finished with error: {}", cardTypeResult.getError().getMessage());
            return new ResultData<>(cardTypeResult.getError(), false);
        }

        Processing processing = requestDTO.getParams().getProcessing();

        if (processing.equals(Processing.DAVR_MASTER_CARD)
                || processing.equals(Processing.SVGATE_AUTOPAY)) {
            var message = "Processing: %s not serviced!".formatted(processing);
            log.error(message);
            return new ResultData<>(ResultState.NotProcessed, message);
        }

        var request = requestBuilder(requestDTO, cardTypeResult);

        var response = monitoringClientService.monitoringV3(request);

        if (!response.getStatus()) {
            log.error("Card history method finished with error: {}", response.getError());
            return new ResultData<>(response.getError(), false);
        }

        if (response.getData().getError() != null) {
            log.error("Card history method finished with error: {}", response.getData().getError());
            return new ResultData<>(response.getData().getError(), false);
        }

        var result = response.getData().getData();

        var lastResponse = ResultData.ok(
                result
                        .stream()
                        .map(CardHistoryResponseDTO::new)
                        .toList()
        );

        log.info("Finished card history method with response size: {}", lastResponse.getResult().size());
        return lastResponse;
    }

    private static MonitoringBaseRequest requestBuilder(CardHistoryRequestDTO requestDTO, SimpleResponse<MonitoringCardType> cardTypeResult) {
        var today = DateUtils.getDateNow();
        var beforeMonthDay = DateUtils.getDateNow().minusDays(30);

        var startDate = DateUtils.LocalDateConvertToFormat(beforeMonthDay, "yyyyMMdd");
        var endDate = DateUtils.LocalDateConvertToFormat(today, "yyyyMMdd");


        String card;
        Processing processing = requestDTO.getParams().getProcessing();

        if (processing.equals(Processing.HUMO) || processing.equals(Processing.DAVR_VISA))
            card = CardUtils.decryptPan(requestDTO.getParams().getCard());
        else
            card = requestDTO.getParams().getCard();

        return new MonitoringBaseRequest(
                cardTypeResult.getData(),
                card,
                startDate,
                endDate
        );
    }


}
