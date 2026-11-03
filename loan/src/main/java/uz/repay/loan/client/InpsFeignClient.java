package uz.repay.loan.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.InpsInfo;
import uz.repay.loan.client.configuration.InpsClientConfiguration;

@FeignClient(name = "inps", url = "${external.inps.url}", configuration = InpsClientConfiguration.class)
public interface InpsFeignClient {

    @GetMapping("/info")
    ResultData<InpsInfo> getDataByPersonCode(@RequestParam("personCode") String personCode);
}
