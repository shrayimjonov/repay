package uz.repay.payment.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uz.repay.payment.client.configuration.BillingConfiguration;
import uz.repay.payment.model.dto.billing.request.CancelRequest;
import uz.repay.payment.model.dto.billing.request.CheckRequest;
import uz.repay.payment.model.dto.billing.request.PayRequest;
import uz.repay.payment.model.dto.billing.response.BillingBaseResponse;
import uz.repay.payment.model.dto.billing.response.PaymentInfoResponse;
import uz.repay.payment.model.dto.billing.response.result.DataResult;
import uz.repay.payment.model.dto.billing.response.result.SRData;

@FeignClient(
        value = "billing",
        url = "${external.billing.url}/services/bmms/api",
        configuration = BillingConfiguration.class
)
public interface BillingFeignClient {

    @PostMapping("/agent/check/v3")
    BillingBaseResponse<Object> check(@RequestBody CheckRequest request);

    @PostMapping("/agent/pay/v3")
    BillingBaseResponse<DataResult<SRData>> pay(@RequestBody PayRequest request);

    @PostMapping("/agent/cancel/v3")
    BillingBaseResponse<Object> cancel(@RequestBody CancelRequest request);

    @GetMapping("/transactions/getByRequestTrId/{requestTrId}")
    PaymentInfoResponse getPayInfoByTranId(@PathVariable("requestTrId") String requestTrId);


}
