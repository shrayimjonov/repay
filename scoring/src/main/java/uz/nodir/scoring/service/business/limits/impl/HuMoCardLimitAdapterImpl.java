package uz.nodir.scoring.service.business.limits.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.scoring.model.dto.limit.response.CardLimitResponseDTO;
import uz.nodir.scoring.service.business.limits.CardLimitAdapter;
import uz.nodir.scoring.service.core.LimitDAO;
import uz.nodir.scoring.service.web.HuMoGateClientService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.utils.CardUtils;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class HuMoCardLimitAdapterImpl implements CardLimitAdapter {
    private final HuMoGateClientService huMoGateClientService;
    private final LimitDAO limitDAO;

    @Override
    public SimpleResponse<List<CardLimitResponseDTO>> getCardLimits(String pan) {
        log.info("Check limit by pan: {}", pan);

        var huMoResult = huMoGateClientService.getCardLimits(CardUtils.decryptPan(pan));

        if (!huMoResult.getStatus())
            return new SimpleResponse<>(huMoResult.getError().getCode(), huMoResult.getError().getMessage());

        if (huMoResult.getData().getError() != null)
            return new SimpleResponse<>(huMoResult.getData().getError().getCode(), huMoResult.getData().getError().getMessage());

        var result = huMoResult.getData().getBody().getResult();

        if (result.getEvent().getLimits() == null || result.getEvent().getLimits().isEmpty()) {
            log.info("Card limit list is empty and will be return empty list");
            return new SimpleResponse<>(List.of());
        }

        var resultData = result
                .getEvent()
                .getLimits()
                .stream()
                .map(limit ->
                        new CardLimitResponseDTO(
                                CardUtils.maskPanDigits(result.getEvent().getKeyValue()),
                                findLimitByType(limit.getLimitType()),
                                limit.getLimitDateTo(),
                                limit.getLimitValue(),
                                null
                        )
                )
                .toList();

        log.info("Card limit result: {}", resultData);
        return new SimpleResponse<>(resultData);
    }

    @Override
    public Processing getPC() {
        return Processing.HUMO;
    }

    public String findLimitByType(String type) {
        var entity = limitDAO
                .getAll()
                .stream()
                .filter(limit -> limit.getProcessing().equals(Processing.HUMO) && limit.getType().equals(type))
                .findFirst()
                .orElse(null);

        return entity != null ? entity.getDescription() : type;
    }

}
