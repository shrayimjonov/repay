package uz.nodir.scoring.service.business.limits;

import uz.nodir.scoring.model.dto.limit.response.CardLimitResponseDTO;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;

import java.util.List;

public interface CardLimitAdapter {

    SimpleResponse<List<CardLimitResponseDTO>> getCardLimits(String pan);

    Processing getPC();
}
