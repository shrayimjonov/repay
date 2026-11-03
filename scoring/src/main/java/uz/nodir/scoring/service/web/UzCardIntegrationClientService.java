package uz.nodir.scoring.service.web;

import uz.nodir.scoring.model.dto.uzCard.response.UzCardBaseResponse;
import uz.nodir.scoring.model.dto.uzCard.response.result.CardLimitResult;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.List;

public interface UzCardIntegrationClientService {
    SimpleResponse<UzCardBaseResponse<List<CardLimitResult>>> getCardLimits(String token);
}
