package uz.repay.payment.service.web;

import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.model.dto.uzcard.request.TransPayDto;
import uz.repay.payment.model.dto.uzcard.request.UzCardBaseRequest;
import uz.repay.payment.model.dto.uzcard.request.params.TransPayParam;
import uz.repay.payment.model.dto.uzcard.request.params.TransPayPurposeParam;
import uz.repay.payment.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.payment.model.dto.uzcard.response.result.TransPayPurposeResult;
import uz.repay.payment.model.dto.uzcard.response.result.UzCardBasePaymentResult;

import java.util.List;

public interface UzCardIntegrationService {
    SimpleResponse<UzCardBaseResponse<TransPayPurposeResult>> pay(UzCardBaseRequest<TransPayPurposeParam> request);

    SimpleResponse<UzCardBaseResponse<TransPayPurposeResult>> pay2(TransPayDto request);

    SimpleResponse<UzCardBaseResponse<List<UzCardBasePaymentResult>>> reverse(String refNum);

    SimpleResponse<UzCardBaseResponse<List<UzCardBasePaymentResult>>> getPaymentByRefNum(String refNum);

    SimpleResponse<UzCardBaseResponse<List<UzCardBasePaymentResult>>> getPaymentById(String paymentId);
}
