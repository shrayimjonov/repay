package uz.repay.payment.service.web;

import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.model.dto.humo.request.*;
import uz.repay.payment.model.dto.humo.response.HuMoBaseEmptyResponse;
import uz.repay.payment.model.dto.humo.response.HuMoBasePaymentResponse;

import java.util.Map;

public interface HuMoGatewayService {
    SimpleResponse<HuMoBasePaymentResponse> debit(HuMoDebitRequest request);

    SimpleResponse<HuMoBasePaymentResponse> debitConfirm(HuMoDebitConfirmRequest request);

    SimpleResponse<HuMoBasePaymentResponse> getPaymentByExt(HuMoGetPaymentByExtRequest request);

    SimpleResponse<HuMoBasePaymentResponse> getPaymentById(HuMoGetPaymentByIdRequest request);

    SimpleResponse<HuMoBaseEmptyResponse> returnByRefNum(HuMoReturnPaymentByExtRequest request);

    SimpleResponse<HuMoBaseEmptyResponse> returnById(HuMoReturnPaymentByIdRequest request);

    SimpleResponse<HuMoBaseEmptyResponse> cancelByExt(HuMoCancelPaymentByExtRequest request);

    SimpleResponse<HuMoBaseEmptyResponse> cancelById(HuMoCancelPaymentByIdRequest request);

    SimpleResponse<Map<String, Object>> executeTransaction(ExecuteTransactionRequest request);
}
