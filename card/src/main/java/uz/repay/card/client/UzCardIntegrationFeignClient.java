package uz.repay.card.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.card.client.configuration.UzCardIntegrationClientConfiguration;
import uz.repay.card.model.dto.uzcard.request.*;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.*;
import uz.repay.common.model.dto.response.core.CoreError;

import java.util.List;

@FeignClient(value = "uzcard-integration", url = "${external.uzcard.integration-url}", configuration = UzCardIntegrationClientConfiguration.class)
public interface UzCardIntegrationFeignClient {

    @PostMapping("/info/otp")
    UzCardBaseResponse<OTPResult> OTPSend(OTPRequest request);

    @PostMapping("/info/verify")
    UzCardBaseResponse<CardBaseResult> OTPVerify(VerifyRequest request);

    @PostMapping("/cardGetBalance")
    UzCardBaseResponse<BalanceResult> getCardsBalance(BalanceRequest request);

    @PostMapping("/cardGetList")
    UzCardBaseResponse<List<CardBaseResult>> getCardsInfo(BalanceRequest request);

    @PostMapping("/p2pinfo")
    UzCardBaseResponse<P2PInfoResult> getP2PInfo(P2PInfoRequest request);

    @PostMapping("/info/checkCardToPinfl")
    UzCardBaseResponse<CoreError> checkCardIsBelongToPersonCode(CheckCard2PinflRequest request);

    @PostMapping("/cardsListToken")
    UzCardBaseResponse<List<CardsListToken>> getCardsByPhone(@Valid CheckCardsByPhoneRequest request);
}
