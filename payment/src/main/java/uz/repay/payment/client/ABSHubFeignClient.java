package uz.repay.payment.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.payment.client.configuration.ABSHubFeignConfiguration;
import uz.repay.payment.model.dto.absHub.request.AbsHubTransactionReverseRequest;
import uz.repay.payment.model.dto.absHub.response.AbsHubTransactionReverseResponse;


@FeignClient(name = "absHub", url = "${external.abs-hub.url}", path = "/hub", configuration = ABSHubFeignConfiguration.class)
public interface ABSHubFeignClient {

    @PostMapping("/api/v1/general/reverse")
    ResultData<AbsHubTransactionReverseResponse> reverse(@Valid AbsHubTransactionReverseRequest request);


}
