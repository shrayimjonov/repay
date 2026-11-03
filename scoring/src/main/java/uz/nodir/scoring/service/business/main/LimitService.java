package uz.nodir.scoring.service.business.main;

import jakarta.validation.constraints.NotNull;
import uz.nodir.scoring.model.dto.limit.request.LimitBaseRequestDTO;
import uz.nodir.scoring.model.dto.limit.response.LimitBaseResponseDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

public interface LimitService {

    ResultData<CoreError> create(@NotNull LimitBaseRequestDTO requestDTO);

    ResultData<CoreError> update(@NotNull Long id, @NotNull LimitBaseRequestDTO requestDTO);

    ResultData<List<LimitBaseResponseDTO>> findAll();

    ResultData<CoreError> deleteById(@NotNull Long id);

    ResultData<CoreError> evictCache();

    ResultData<LimitBaseResponseDTO> findById(@NotNull Long id);
}
