package uz.repay.payment.service.web;

import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.payment.model.dto.absHub.request.AbsHubTransactionReverseRequest;
import uz.repay.payment.model.dto.absHub.response.AbsHubTransactionReverseResponse;

public interface AbsHubClientService {

    ResultData<AbsHubTransactionReverseResponse> reverse(AbsHubTransactionReverseRequest request);
}
