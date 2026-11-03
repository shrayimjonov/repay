package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.card.client.configuration.HuMoGatewayClientConfiguration;
import uz.repay.card.model.dto.humoGateway.request.HuMoRecoActiveRequest;
import uz.repay.card.model.dto.humoGateway.request.HuMoRecoConfirmRequest;
import uz.repay.card.model.dto.humoGateway.response.HuMoBankBins;
import uz.repay.card.model.dto.humoGateway.response.HuMoRecoActiveResponse;
import uz.repay.card.model.dto.humoGateway.response.HuMoRecoConfirmResponse;

import java.util.List;

@FeignClient(value = "humo-gateway", url = "${external.humo-gateway.url}", configuration = HuMoGatewayClientConfiguration.class)
public interface HuMoGatewayFeignClient {

    @PostMapping("/payment/recoCreate")
    HuMoRecoActiveResponse recoActive(HuMoRecoActiveRequest request);

    @PostMapping("/payment/recoFinish")
    HuMoRecoConfirmResponse recoConfirm(HuMoRecoConfirmRequest request);

    @GetMapping("/entity/bins")
    List<HuMoBankBins> getBankBins();

}
