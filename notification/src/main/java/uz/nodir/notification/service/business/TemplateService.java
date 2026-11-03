package uz.nodir.notification.service.business;

import lombok.NonNull;
import uz.nodir.notification.model.dto.template.request.TemplateBaseRequestDTO;
import uz.nodir.notification.model.dto.template.request.TemplateFilterRequestDTO;
import uz.nodir.notification.model.dto.template.response.TemplateBaseResponseDTO;
import uz.nodir.notification.model.dto.template.response.TemplateFilterResponseDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

public interface TemplateService {
    ResultData<TemplateBaseResponseDTO> create(@NonNull TemplateBaseRequestDTO requestDTO);

    ResultData<CoreError> evict();

    ResultData<TemplateFilterResponseDTO> filter(@NonNull TemplateFilterRequestDTO requestDTO);

    ResultData<TemplateBaseResponseDTO> findById(@NonNull Long id);

    ResultData<CoreError> deleteById(@NonNull Long id);

    ResultData<CoreError> update(@NonNull Long id, @NonNull TemplateBaseRequestDTO requestDTO);
}
