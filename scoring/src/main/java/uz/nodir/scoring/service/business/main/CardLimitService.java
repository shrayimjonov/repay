package uz.nodir.scoring.service.business.main;

import jakarta.validation.constraints.NotNull;
import uz.nodir.scoring.model.dto.limit.request.CardLimitRequestDTO;
import uz.nodir.scoring.model.dto.limit.response.CardLimitResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

public interface CardLimitService {

    ResultData<List<CardLimitResponseDTO>> getCardLimits(@NotNull CardLimitRequestDTO requestDTO);
}
