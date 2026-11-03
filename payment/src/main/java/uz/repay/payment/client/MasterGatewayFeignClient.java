package uz.repay.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.payment.client.configuration.MasterGatewayClientConfiguration;
import uz.repay.payment.model.dto.master.request.*;
import uz.repay.payment.model.dto.master.response.MasterPaymentResponse;

@FeignClient(value = "master-gate", path = "/connect", url = "${external.master-gate.url}", configuration = MasterGatewayClientConfiguration.class)
public interface MasterGatewayFeignClient {

    @PostMapping("/api/v1/payment/debit/token")
    ResultData<MasterPaymentResponse> debit(MasterBaseRequest<MasterDebitRequest> request);

    @PostMapping("/api/v1/sync/byExtId")
    ResultData<MasterPaymentResponse> syncByExtId(MasterBaseRequest<MasterSyncByExtIdRequest> request);

    @PostMapping("/api/v1/sync/byRRN")
    ResultData<MasterPaymentResponse> syncByRRN(MasterBaseRequest<MasterSyncByRRNRequest> request);

    @PostMapping("/api/v1/reverse/byExtId")
    ResultData<MasterPaymentResponse> reverseByExtId(MasterBaseRequest<MasterReverseByExtIdRequest> request);


}
