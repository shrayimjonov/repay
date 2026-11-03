package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uz.repay.card.client.configuration.DavrMobileClientConfiguration;
import uz.repay.card.model.dto.dm.response.CardsByPINFLResponse;

@FeignClient(name = "dm", url = "${external.dm.integration-url}", configuration = DavrMobileClientConfiguration.class)
public interface DavrMobileFeignClient {

    @GetMapping("/internal/cards/pinfl/{personCode}")
    CardsByPINFLResponse getClientCardsByPINFL(@PathVariable("personCode") String personCode);
}
