package uz.repay.payment.service.web;

import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.model.dto.visa.request.VisaHistoryRequest;
import uz.repay.payment.model.dto.visa.request.VisaTranBaseRequest;
import uz.repay.payment.model.dto.visa.response.VisaHistoryResponse;
import uz.repay.payment.model.dto.visa.response.VisaTranBaseResponse;

public interface VisaIntegrationService {

    SimpleResponse<ResultData<VisaTranBaseResponse>> debit(VisaTranBaseRequest request);

    SimpleResponse<ResultData<VisaTranBaseResponse>> reverse(VisaTranBaseRequest request);

    SimpleResponse<VisaHistoryResponse> history(VisaHistoryRequest request);
}
