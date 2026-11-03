package uz.repay.payment.service.web;

import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.payment.NCICourseResponse;
import uz.repay.payment.model.dto.nciBilling.request.NCIForexTranRequest;
import uz.repay.payment.model.dto.nciBilling.request.NCIPayRequest;
import uz.repay.payment.model.dto.nciBilling.request.NCIReverseDTO;
import uz.repay.payment.model.dto.nciBilling.request.NCISimpleTranRequest;
import uz.repay.payment.model.dto.nciBilling.response.*;

import java.util.List;

public interface NCIBillingService {

    SimpleResponse<NCICheckResponse<NCIBaseResponse>> checkPaymentFromABS(String paymentId);

    @Deprecated
    SimpleResponse<NCIBaseResponse> reverseABSTransaction(NCIReverseDTO requestDTO);

    SimpleResponse<ResultData<ProblemResponse>> pay(NCIPayRequest request);

    SimpleResponse<NCIBaseResponse> doForex(NCIForexTranRequest request);

    SimpleResponse<NCIBaseResponse> doSimpleTransaction(NCISimpleTranRequest request);

    SimpleResponse<NCIDayResponse> getBankDay();

    SimpleResponse<ResultData<List<NCICourseResponse>>> getCourse();

    ResultData<NCITransactionResponse> getTransactionById(String absTranId);
}
