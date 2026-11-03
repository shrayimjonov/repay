package uz.nodir.notification.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.nodir.notification.client.configuration.LoanServiceFeignConfiguration;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;

import java.util.List;

@FeignClient(name = "loan", path = "/loan", configuration = LoanServiceFeignConfiguration.class)
public interface LoanFeignClient {
    @PostMapping("/api/v1/internal/balance")
    ResultData<LoanInfoResponse> getLoanBalance(LoanSimpleRequestDTO requestDTO);

    @GetMapping("/api/v1/internal/takeLoanFeaturedCreators")
    ResultData<List<String>> takeLoanFeaturedCreators(@RequestParam("loanExtId") String loanExtId);

}
