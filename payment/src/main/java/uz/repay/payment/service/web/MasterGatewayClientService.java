package uz.repay.payment.service.web;

import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.model.dto.master.request.MasterDebitRequest;
import uz.repay.payment.model.dto.master.request.MasterReverseByExtIdRequest;
import uz.repay.payment.model.dto.master.request.MasterSyncByExtIdRequest;
import uz.repay.payment.model.dto.master.request.MasterSyncByRRNRequest;
import uz.repay.payment.model.dto.master.response.MasterPaymentResponse;

public interface MasterGatewayClientService {
    SimpleResponse<ResultData<MasterPaymentResponse>> debit(MasterDebitRequest request);

    SimpleResponse<ResultData<MasterPaymentResponse>> syncByExtId(MasterSyncByExtIdRequest request);

    SimpleResponse<ResultData<MasterPaymentResponse>> syncByRRN(MasterSyncByRRNRequest request);

    SimpleResponse<ResultData<MasterPaymentResponse>> reverseByExtId(MasterReverseByExtIdRequest request);
}
