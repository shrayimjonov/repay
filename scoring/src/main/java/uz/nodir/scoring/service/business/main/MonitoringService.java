package uz.nodir.scoring.service.business.main;

import jakarta.validation.constraints.NotNull;
import uz.nodir.scoring.model.dto.history.request.CardHistoryRequestDTO;
import uz.nodir.scoring.model.dto.history.response.CardHistoryResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

public interface MonitoringService {
    ResultData<List<CardHistoryResponseDTO>> takeCardHistory(@NotNull CardHistoryRequestDTO requestDTO);
}
