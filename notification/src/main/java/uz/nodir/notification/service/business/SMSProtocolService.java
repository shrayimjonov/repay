package uz.nodir.notification.service.business;

import lombok.NonNull;
import uz.nodir.notification.model.dto.protocol.request.ProtocolFilterRequestDTO;
import uz.nodir.notification.model.dto.protocol.response.ProtocolFilterResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

public interface SMSProtocolService {
    ResultData<ProtocolFilterResponseDTO> findMessagesByFilter(@NonNull ProtocolFilterRequestDTO requestDTO);
}
