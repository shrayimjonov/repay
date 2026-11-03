package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.repay.card.client.configuration.DavrMobile2ClientConfiguration;
import uz.repay.card.model.dto.dm2.response.DM2CardsByPinflResponse;

import java.util.List;

@FeignClient(name = "dm2", url = "${external.dm2.url}", configuration = DavrMobile2ClientConfiguration.class)
public interface DavrMobile2FeignClient {

    @GetMapping("/card-token")
    List<DM2CardsByPinflResponse> getClientCardsByPINFL(@RequestParam("pinfl") String personCode);
}
