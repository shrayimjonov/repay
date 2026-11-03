package uz.repay.card.service.business;

import lombok.NonNull;
import uz.repay.card.model.dto.core.request.epos.EPosCreateRequestDTO;
import uz.repay.card.model.dto.core.request.epos.EPosExceptionalDTO;
import uz.repay.card.model.dto.core.response.epos.EposResponseListDTO;
import uz.repay.common.model.dto.response.cardService.EPosResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

public interface EPosService {
    ResultData<EPosResponseDTO> create(@NonNull EPosCreateRequestDTO requestDTO);

    ResultData<EPosResponseDTO> update(@NonNull Long id, @NonNull EPosCreateRequestDTO requestDTO);

    ResultData<List<EposResponseListDTO>> getAll();

    ResultData<EPosResponseDTO> delete(@NonNull Long id);

    ResultData<String> evictCache();//test

    void makeExceptional(EPosExceptionalDTO requestDTO);
}
