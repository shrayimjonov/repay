package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import uz.repay.card.client.configuration.ElmaClientConfiguration;
import uz.repay.card.model.dto.elma.request.ElmaVisaCardFindRequest;
import uz.repay.card.model.dto.elma.response.ElmaVisaCardFindResponse;

@FeignClient(name = "elma", url = "${external.elma.url}", configuration = ElmaClientConfiguration.class)
public interface ElmaFeignClient {

    @GetMapping("/rko/rko_GetVISAClientsCards")
    ElmaVisaCardFindResponse getClientVisaCardsByCode(ElmaVisaCardFindRequest  request);
}
