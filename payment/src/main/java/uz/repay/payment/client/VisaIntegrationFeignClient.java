package uz.repay.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.payment.client.configuration.VisaIntegrationFeignConfiguration;
import uz.repay.payment.model.dto.visa.request.VisaHistoryRequest;
import uz.repay.payment.model.dto.visa.request.VisaTranBaseRequest;
import uz.repay.payment.model.dto.visa.response.VisaHistoryResponse;
import uz.repay.payment.model.dto.visa.response.VisaTranBaseResponse;

@FeignClient(name = "visa-integration", url = "${external.visa-integration.url}", configuration = VisaIntegrationFeignConfiguration.class)
public interface VisaIntegrationFeignClient {

    @PostMapping("/api/v1/tran/loanRepay")
    ResultData<VisaTranBaseResponse> debit(VisaTranBaseRequest request);

    @PostMapping("/api/v1/tran/loanRepayReverse")
    ResultData<VisaTranBaseResponse> reverse(VisaTranBaseRequest request);

    @PostMapping("/findVisaHistory")
    VisaHistoryResponse history(VisaHistoryRequest request);

}
