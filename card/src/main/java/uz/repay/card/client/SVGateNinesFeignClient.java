package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.card.client.configuration.SVGateNinesClientConfiguration;
import uz.repay.card.model.dto.svgate.request.SVGateBaseRequest;
import uz.repay.card.model.dto.svgate.request.params.SVGateAutoPaySubscribeParam;
import uz.repay.card.model.dto.svgate.request.params.SVGateOTPSendParam;
import uz.repay.card.model.dto.svgate.request.params.SVGateOTPVerifyParam;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.dto.uzcard.response.result.OTPResult;
import uz.repay.common.model.dto.response.core.CoreError;

@FeignClient(value = "svGate-nines", url = "${external.svgate-nines.url}", path = "/api/jsonrpc", configuration = SVGateNinesClientConfiguration.class)
public interface SVGateNinesFeignClient {
    @PostMapping
    UzCardBaseResponse<CoreError> autoPaySubscribe(SVGateBaseRequest<SVGateAutoPaySubscribeParam> request);

    @PostMapping
    UzCardBaseResponse<OTPResult> OTPSend(SVGateBaseRequest<SVGateOTPSendParam> request);

    @PostMapping
    UzCardBaseResponse<CardBaseResult> OTPVerify(SVGateBaseRequest<SVGateOTPVerifyParam> request);
}
