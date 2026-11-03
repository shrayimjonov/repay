package uz.nodir.notification.service.business;

import lombok.NonNull;
import uz.nodir.notification.model.dto.message.request.NotificationBaseRequestDTO;
import uz.nodir.notification.model.dto.message.request.NotificationDebtMessageRequestDTO;
import uz.nodir.notification.model.dto.message.request.NotificationMobileLinkRequestDTO;
import uz.repay.common.model.dto.request.kafka.CardAttachedEventDTO;
import uz.repay.common.model.dto.request.kafka.CardConfirmationEventDTO;
import uz.repay.common.model.dto.request.kafka.NotificationDebitPayEventDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

public interface MessageService {

    ResultData<CoreError> debitPay(@NonNull NotificationDebitPayEventDTO requestDTO);

    ResultData<CoreError> debt(@NonNull NotificationDebtMessageRequestDTO requestDTO);

    ResultData<CoreError> confirmation(@NonNull CardConfirmationEventDTO requestDTO);

    ResultData<CoreError> mib(@NonNull NotificationBaseRequestDTO requestDTO);

    ResultData<CoreError> notice(@NonNull NotificationBaseRequestDTO requestDTO);

    ResultData<CoreError> cardAttach(@NonNull CardAttachedEventDTO requestDTO);

    ResultData<CoreError> mobileDeepLink(@NonNull NotificationMobileLinkRequestDTO requestDTO);
}
