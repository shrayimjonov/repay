package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.card.client.configuration.SVGateClientConfiguration;
import uz.repay.card.model.dto.svgate.request.SVGateBaseRequest;
import uz.repay.card.model.dto.svgate.request.params.*;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.*;
import uz.repay.common.model.dto.response.core.CoreError;

import java.util.List;

@FeignClient(value = "svGate", url = "${external.svgate.url}", path = "/api/jsonrpc", configuration = SVGateClientConfiguration.class)
public interface SVGateFeignClient {

    @PostMapping
    UzCardBaseResponse<BalanceResult> getCardsBalance(SVGateBaseRequest<SVGateCardsGetParam> request);

    @PostMapping
    UzCardBaseResponse<List<CardBaseResult>> getCardsInfo(SVGateBaseRequest<SVGateCardsGetParam> request);

    @PostMapping
    UzCardBaseResponse<P2PInfoResult> getP2PInfo(SVGateBaseRequest<SVGateP2PInfoParam> request);

    @PostMapping
    UzCardBaseResponse<CoreError> checkCardIsBelongToPersonCode(SVGateBaseRequest<SVGateCardCheckPINFLParam> request);

    @PostMapping
    UzCardBaseResponse<List<CardsListToken>> getCardsByPhone(SVGateBaseRequest<SVGateCardsByPhoneParam> request);

}
