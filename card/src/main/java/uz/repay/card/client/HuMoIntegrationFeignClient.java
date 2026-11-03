package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.repay.card.client.configuration.HumoClientConfiguration;
import uz.repay.card.model.dto.humo.request.HuMoCardConfirmRequest;
import uz.repay.card.model.dto.humo.request.HuMoCardVerifyRequest;
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequest;
import uz.repay.card.model.dto.humo.request.HumoCardsByPINFLRequest;
import uz.repay.card.model.dto.humo.response.HuMoCardBaseResponse;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.model.dto.humo.response.HumoCardsByPINFLResponse;
import uz.repay.card.model.dto.humo.response.HumoTokenResponse;
import uz.repay.card.model.dto.humo.response.result.HuMoCardConfirmResult;
import uz.repay.card.model.dto.humo.response.result.HuMoCardVerifyResult;
import uz.repay.card.model.dto.humo.response.result.HuMoP2PInfoResult;
import uz.repay.common.model.dto.response.core.ResultData;



@FeignClient(value = "humo-integration", url = "${external.humo.integration-url}", configuration = HumoClientConfiguration.class)
public interface HuMoIntegrationFeignClient {

    @PostMapping("/cards")
    HumoCardInfoResponse getCardInfo(HumoCardInfoRequest request);

    @GetMapping("/maskedPan")
    HumoTokenResponse getCardToken(@RequestParam("cardNumber") String cardNumber);

    @PostMapping("/cardsByPINFL")
    HumoCardsByPINFLResponse getClientCardsByPINFL(HumoCardsByPINFLRequest request);

    @PostMapping("/api/v1/add/verify")
    ResultData<HuMoCardBaseResponse<HuMoCardVerifyResult>> verify(HuMoCardVerifyRequest request);

    @PostMapping("/api/v1/add/confirm")
    ResultData<HuMoCardBaseResponse<HuMoCardConfirmResult>> confirm(HuMoCardConfirmRequest request);

    @PostMapping("/p2pInfo")
    HuMoCardBaseResponse<HuMoP2PInfoResult> p2pInfo(HumoCardInfoRequest request);
}
