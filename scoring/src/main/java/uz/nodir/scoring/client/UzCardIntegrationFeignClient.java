package uz.nodir.scoring.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.nodir.scoring.client.configuration.UzCardIntegrationFeignConfiguration;
import uz.nodir.scoring.model.dto.uzCard.request.UzCardLimitRequest;
import uz.nodir.scoring.model.dto.uzCard.response.UzCardBaseResponse;
import uz.nodir.scoring.model.dto.uzCard.response.result.CardLimitResult;

import java.util.List;


@FeignClient(name = "uzcard-integration", url = "${external.uzcard-integration.url}", configuration = UzCardIntegrationFeignConfiguration.class)
public interface UzCardIntegrationFeignClient {

    @PostMapping("/limit/card")
    UzCardBaseResponse<List<CardLimitResult>> getCardLimits(UzCardLimitRequest request);

}
