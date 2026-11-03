package uz.nodir.scoring.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.nodir.scoring.client.configuration.HuMoGatewayFeignConfiguration;
import uz.nodir.scoring.model.dto.huMoGateway.request.HuMoGateCardLimitRequest;
import uz.nodir.scoring.model.dto.huMoGateway.response.HuMoGateCardLimitResponse;


@FeignClient(name = "humo-gateway", url = "${external.humo-gateway.url}", configuration = HuMoGatewayFeignConfiguration.class)
public interface HuMoGatewayFeignClient {

    @PostMapping("/limit/v2/card")
    HuMoGateCardLimitResponse getCardLimits(HuMoGateCardLimitRequest request);

}
