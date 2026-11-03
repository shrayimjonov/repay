package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.card.client.configuration.HuMoMiddlewareClientConfiguration;
import uz.repay.card.model.dto.huMoMiddleware.request.HuMoMiddlewareBaseRequest;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoCardV3InfoRequestParam;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoMaskedPanParam;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoRealPanRequest;
import uz.repay.card.model.dto.huMoMiddleware.response.result.HuMoMaskedPanResult;
import uz.repay.card.model.dto.huMoMiddleware.response.result.HuMoRealPanResult;
import uz.repay.card.model.dto.humo.response.HuMoCardBaseResponse;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.model.dto.humo.response.result.HuMoP2PInfoResult;

@FeignClient(value = "humo-middleware", url = "${external.humo.url}", configuration = HuMoMiddlewareClientConfiguration.class)
public interface HuMoMiddlewareFeignClient {
    @PostMapping("/v3/iiacs/card")
    HuMoCardBaseResponse<HumoCardInfoResponse.HumoCardResult> getCustomerCardInfo(HuMoMiddlewareBaseRequest<HuMoCardV3InfoRequestParam> request);

    @PostMapping("/v2/pm/masked-pan")
    HuMoCardBaseResponse<HuMoMaskedPanResult> getCardToken(HuMoMiddlewareBaseRequest<HuMoMaskedPanParam> request);

    @PostMapping("/v3/iiacs/card2")
    HuMoCardBaseResponse<HuMoP2PInfoResult> p2pInfo(HuMoMiddlewareBaseRequest<HuMoCardV3InfoRequestParam> request);

    @PostMapping("/v2/pm/real-pan")
    HuMoCardBaseResponse<HuMoRealPanResult> getCardPan(HuMoMiddlewareBaseRequest<HuMoRealPanRequest> request);
}
