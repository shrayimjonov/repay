package uz.nodir.notification.service.core;

import uz.nodir.notification.model.dto.core.response.TemplateTypeResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

public interface InfoService {
    ResultData<List<TemplateTypeResponseDTO>> takeTemplateTypes();
}
