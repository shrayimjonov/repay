package uz.nodir.notification.service.business;

import lombok.NonNull;
import uz.repay.common.model.dto.request.kafka.CourtEventDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

public interface CustomMessageService {

    ResultData<CoreError> courtMessage(@NonNull CourtEventDTO eventDTO);
}
