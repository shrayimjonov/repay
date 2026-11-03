package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.card.client.configuration.HuMoMiddlewareSlowerClientConfiguration;
import uz.repay.card.model.dto.huMoMiddleware.request.HuMoMiddlewareBaseRequest;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoCheckCardToPINFLParam;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoOTPSendParam;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoOTPVerifyParam;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoPersonCodeParam;
import uz.repay.card.model.dto.huMoMiddleware.response.result.HuMoCardsByPinflResult;
import uz.repay.card.model.dto.humo.response.HuMoCardBaseResponse;
import uz.repay.card.model.dto.humo.response.HumoCardsByPINFLResponse;
import uz.repay.card.model.dto.humo.response.result.HuMoCardConfirmResult;
import uz.repay.card.model.dto.humo.response.result.HuMoCardVerifyResult;

@FeignClient(value = "huMo-middleware-slower", url = "${external.humo-mware.url}", configuration = HuMoMiddlewareSlowerClientConfiguration.class)
public interface HuMoMWareSlowResponseFeignClient {
    @PostMapping("/cs/v1/customer/cards/by-person-code")
    HuMoCardBaseResponse<HumoCardsByPINFLResponse.PINFLResult> getClientCardsByPINFL(HuMoMiddlewareBaseRequest<HuMoPersonCodeParam> request);

    @PostMapping("/v1/send/sms")
    HuMoCardBaseResponse<HuMoCardVerifyResult> verify(HuMoMiddlewareBaseRequest<HuMoOTPSendParam> request);

    @PostMapping("/v1/send/code")
    HuMoCardBaseResponse<HuMoCardConfirmResult> confirm(HuMoMiddlewareBaseRequest<HuMoOTPVerifyParam> request);

    @PostMapping("/cs/v1/customer/cards/by-person-code/pan")
    HuMoCardBaseResponse<String> checkCardToPINFL(HuMoMiddlewareBaseRequest<HuMoCheckCardToPINFLParam> request);

    @PostMapping("/cs/v1/card-info-by-person-code")
    HuMoCardBaseResponse<HuMoCardsByPinflResult> getClientCardsByPINFLV2(HuMoMiddlewareBaseRequest<HuMoPersonCodeParam> request);

}
