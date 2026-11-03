package uz.nodir.file.service.business;

import jakarta.annotation.Nonnull;
import uz.nodir.file.model.dto.location.request.LocationBaseRequestDTO;
import uz.nodir.file.model.dto.location.request.LocationFilterRequestDTO;
import uz.nodir.file.model.dto.location.response.LocationFilterResponseDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

public interface LocationService {

    ResultData<CoreError> create(@Nonnull LocationBaseRequestDTO requestDTO);

    ResultData<LocationFilterResponseDTO> findAll(@Nonnull LocationFilterRequestDTO requestDTO);

    ResultData<CoreError> updateById(@Nonnull Long id, @Nonnull LocationBaseRequestDTO requestDTO);

    ResultData<CoreError> deleteById(@Nonnull Long id);
}
