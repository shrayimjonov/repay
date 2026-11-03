package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.card.client.configuration.SVGateSevensClientConfiguration;
import uz.repay.card.model.dto.svgate.request.SVGateBaseRequest;
import uz.repay.card.model.dto.svgate.request.params.SVGateAutoPayCardsParam;
import uz.repay.card.model.dto.svgate.response.SVGateAutoPayCardsResult;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;

import java.util.List;

@FeignClient(value = "svGate-sevens", url = "${external.svgate-sevens.url}", path = "/api/jsonrpc", configuration = SVGateSevensClientConfiguration.class)
public interface SVGateSevensFeignClient {

    @PostMapping
    UzCardBaseResponse<List<SVGateAutoPayCardsResult>> autoPayCardsGet(SVGateBaseRequest<SVGateAutoPayCardsParam> request);

}
