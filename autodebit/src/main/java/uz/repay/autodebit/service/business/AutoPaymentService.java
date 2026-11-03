package uz.repay.autodebit.service.business;

import uz.repay.common.model.dto.response.core.ResultData;

public interface AutoPaymentService {
    ResultData<String> stop();
}
