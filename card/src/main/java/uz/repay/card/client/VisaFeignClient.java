package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.repay.card.client.configuration.VisaClientConfiguration;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.dto.visa.request.VisaCardInfoRequest;
import uz.repay.card.model.dto.visa.request.VisaCardsInfoRequest;
import uz.repay.card.model.dto.visa.response.VisaBalanceResponse;
import uz.repay.card.model.dto.visa.response.VisaTakeRealPanResponse;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;



@FeignClient(value = "visa-middleware", url = "${external.visa-middleware.url}", configuration = VisaClientConfiguration.class)
public interface VisaFeignClient {

    @PostMapping("/get_list")
    UzCardBaseResponse<List<CardBaseResult>> getCardsInfo(VisaCardsInfoRequest request);

    @PostMapping("/api/v2/info")
    UzCardBaseResponse<CardBaseResult> getCardInfo(VisaCardInfoRequest request);

    @PostMapping("/api/v2/balance")
    VisaBalanceResponse getCardBalance(VisaCardInfoRequest request);

    @GetMapping("/api/v2/realPan")
    ResultData<VisaTakeRealPanResponse> getRealPan(@RequestParam("token") String token);


}
