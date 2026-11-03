package uz.repay.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.repay.payment.client.configuration.UzCardIntegrationFeignConfiguration;
import uz.repay.payment.model.dto.uzcard.request.BalanceRequest;
import uz.repay.payment.model.dto.uzcard.request.P2PInfoRequest;
import uz.repay.payment.model.dto.uzcard.request.TransPayDto;
import uz.repay.payment.model.dto.uzcard.request.UzCardBaseRequest;
import uz.repay.payment.model.dto.uzcard.request.params.TransPayParam;
import uz.repay.payment.model.dto.uzcard.request.params.TransPayPurposeParam;
import uz.repay.payment.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.payment.model.dto.uzcard.response.result.*;

import java.util.List;

@FeignClient(name = "uzcard-integration", url = "${external.uzcard-integration.url}", configuration = UzCardIntegrationFeignConfiguration.class)
public interface UzCardIntegrationFeignClient {

    @PostMapping("/transPayPurpose")
    UzCardBaseResponse<TransPayPurposeResult> transPayPurpose(UzCardBaseRequest<TransPayPurposeParam> request);

    @PostMapping("/transPay")
    UzCardBaseResponse<TransPayPurposeResult> transPay(TransPayDto request);

    @PostMapping("/reverseExtSv")
    UzCardBaseResponse<List<UzCardBasePaymentResult>> reverse(@RequestParam("tranId") String transactionId);

    @GetMapping("/extsv")
    UzCardBaseResponse<List<UzCardBasePaymentResult>> getPaymentById(@RequestParam("sv") String utrnno);

    @GetMapping("/extsv")
    UzCardBaseResponse<List<UzCardBasePaymentResult>> getPaymentByRefNum(@RequestParam("ext") String refNum);

    @PostMapping("/p2pinfo")
    UzCardBaseResponse<P2PInfoResult> getP2PInfo(P2PInfoRequest request);

    @PostMapping("/cardGetList")
    UzCardBaseResponse<List<CardBaseResult>> getCardsInfo(BalanceRequest request);

    @PostMapping("/cardGetBalance")
    UzCardBaseResponse<BalanceResult> getCardsBalance(BalanceRequest request);

}
