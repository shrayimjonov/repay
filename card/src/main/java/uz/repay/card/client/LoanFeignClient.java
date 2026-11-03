package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.card.client.configuration.LoanServiceFeignConfiguration;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;


@FeignClient(name = "loan", path = "/loan", configuration = LoanServiceFeignConfiguration.class)
public interface LoanFeignClient {

    @PostMapping("/api/v2/internal/loan")
    ResultData<CoreError> addLoan(LoanSimpleRequestDTO requestDTO);
}
