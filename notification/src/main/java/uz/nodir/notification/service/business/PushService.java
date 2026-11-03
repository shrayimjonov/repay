package uz.nodir.notification.service.business;

import uz.nodir.notification.model.dto.push.response.PushV2ResponseDTO;
import uz.repay.common.model.dto.request.kafka.LoanPaidEventDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

public interface PushService {
    void receiveLoanDebit(LoanPaidEventDTO eventDTO);

    ResultData<List<PushV2ResponseDTO>> findUserPushes();

    ResultData<CoreError> checkReadPushes();
}
