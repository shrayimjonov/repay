package uz.repay.card.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.repay.card.client.configuration.NCIBillingFeignConfiguration;
import uz.repay.card.model.dto.nciBilling.request.SimpleLoanRequest;
import uz.repay.card.model.dto.nciBilling.response.NCICardsByPersonCodeResponse;
import uz.repay.card.model.dto.nciBilling.response.NCIClientInfoByPersonCode;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;

import java.util.List;


@FeignClient(name = "nciGate", url = "${external.nci-billing.url}", path = "/nci", configuration = NCIBillingFeignConfiguration.class)
public interface NCIBillingFeignClient {

    @GetMapping("/v2/info/cardsByPersonCode")
    ResultData<List<NCICardsByPersonCodeResponse>> getCardsByPersonCode(@RequestParam("person_code") String personCode);


    @PostMapping("/v2/info/loan")
    ResultData<LoanInfoResponse> getLoanInfo(@Valid SimpleLoanRequest request);

    @GetMapping("/v2/info/clientInfoByPINFL")
    ResultData<List<NCIClientInfoByPersonCode>> getClientInfoByPersonCode(@RequestParam("person_code") String personCode);


}
