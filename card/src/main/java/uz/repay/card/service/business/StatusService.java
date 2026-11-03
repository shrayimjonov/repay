package uz.repay.card.service.business;

import lombok.NonNull;
import uz.repay.card.model.dto.core.request.status.StatusRequestDTO;
import uz.repay.common.model.dto.request.card.response.StatusResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;
import java.util.Map;

public interface StatusService {
    ResultData<StatusResponseDTO> create(StatusRequestDTO requestDTO);
    ResultData<StatusResponseDTO> update(Long id, @NonNull StatusRequestDTO requestDTO);
    ResultData<StatusResponseDTO> findById(Long id);
    ResultData<List<StatusResponseDTO>> findAll(String definitionUz);
    ResultData<StatusResponseDTO> deleteById(Long id);
    ResultData<String> evictCache();

    ResultData<List<StatusResponseDTO>> findAll();
}
