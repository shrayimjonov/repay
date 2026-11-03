package uz.repay.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.payment.NCICourseResponse;
import uz.repay.payment.client.configuration.NCIBillingFeignConfiguration;
import uz.repay.payment.model.dto.nciBilling.request.NCIForexTranRequest;
import uz.repay.payment.model.dto.nciBilling.request.NCIPayRequest;
import uz.repay.payment.model.dto.nciBilling.request.NCIReverseDTO;
import uz.repay.payment.model.dto.nciBilling.request.NCISimpleTranRequest;
import uz.repay.payment.model.dto.nciBilling.response.*;

import java.util.List;


@FeignClient(name = "nciGate", url = "${external.nci-billing.url}/nci", configuration = NCIBillingFeignConfiguration.class)
public interface NCIBillingFeignClient {
    @GetMapping("/v2/core/check")
    NCICheckResponse<NCIBaseResponse> check(@RequestParam("paymentId") String paymentId);

    @GetMapping("/v2/core/pay")
    ResultData<ProblemResponse> pay(NCIPayRequest request);

    @PostMapping("/core/reverse")
    NCIBaseResponse reverse(NCIReverseDTO dto);

    @PostMapping("/api/v1/abs/forex")
    NCIBaseResponse doForex(NCIForexTranRequest request);

    @PostMapping("/api/v1/abs/doTran")
    NCIBaseResponse doSimpleTransaction(NCISimpleTranRequest request);

    @GetMapping("/info/day")
    NCIDayResponse getBankDay();

    @GetMapping("/v2/info/course")
    ResultData<List<NCICourseResponse>> getCourse();

    @GetMapping("/info/transaction")
    ResultData<NCITransactionResponse> getTransactionById(@RequestParam("id") String id);



}
