package uz.repay.payment.service.web;

import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.model.dto.billing.request.CancelRequest;
import uz.repay.payment.model.dto.billing.request.CheckRequest;
import uz.repay.payment.model.dto.billing.request.PayRequest;
import uz.repay.payment.model.dto.billing.response.BillingBaseResponse;
import uz.repay.payment.model.dto.billing.response.PaymentInfoResponse;
import uz.repay.payment.model.dto.billing.response.result.DataResult;
import uz.repay.payment.model.dto.billing.response.result.SRData;

public interface BillingService {
    SimpleResponse<BillingBaseResponse<?>> check(CheckRequest request);

    SimpleResponse<BillingBaseResponse<DataResult<SRData>>> pay(PayRequest request);

    SimpleResponse<BillingBaseResponse<?>> cancel(CancelRequest request);

    SimpleResponse<PaymentInfoResponse> getPayment(String requestTrId);
}
