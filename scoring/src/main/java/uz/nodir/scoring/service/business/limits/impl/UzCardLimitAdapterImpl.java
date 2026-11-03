package uz.nodir.scoring.service.business.limits.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.scoring.model.dto.limit.response.CardLimitResponseDTO;
import uz.nodir.scoring.service.business.limits.CardLimitAdapter;
import uz.nodir.scoring.service.core.LimitDAO;
import uz.nodir.scoring.service.web.UzCardIntegrationClientService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UzCardLimitAdapterImpl implements CardLimitAdapter {
    private final UzCardIntegrationClientService uzCardIntegrationClientService;
    private final LimitDAO limitDAO;

    @Override
    public SimpleResponse<List<CardLimitResponseDTO>> getCardLimits(String token) {
        log.info("Check limit by token: {}", token);

        var uzCardResult = uzCardIntegrationClientService.getCardLimits(token);

        if (!uzCardResult.getStatus())
            return new SimpleResponse<>(uzCardResult.getError().getCode(), uzCardResult.getError().getMessage());

        if (uzCardResult.getData().getError() != null)
            return new SimpleResponse<>(uzCardResult.getData().getError().getCode(), uzCardResult.getData().getError().getMessage());

        var result = uzCardResult.getData().getResult();

        var resultData = result
                .stream()
                .map(limit ->
                        new CardLimitResponseDTO(
                                token,
                                findLimitByType(String.valueOf(limit.getLimitId())),
                                LocalDateTime.of(LocalDate.now(), LocalTime.MAX),
                                limit.getLimitAmount(),
                                limit.getUsedAmount()
                        )
                )
                .toList();

        log.info("Card limit result: {}", resultData);
        return new SimpleResponse<>(resultData);
    }

    public String findLimitByType(String type) {
        var entity = limitDAO
                .getAll()
                .stream()
                .filter(limit -> limit.getProcessing().equals(Processing.UZCARD) && limit.getType().equals(type))
                .findFirst()
                .orElse(null);

        return entity != null ? entity.getDescription() : type;
    }


    @Override
    public Processing getPC() {
        return Processing.UZCARD;
    }
}
