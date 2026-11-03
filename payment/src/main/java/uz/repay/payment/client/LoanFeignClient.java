package uz.repay.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.payment.client.configuration.LoanServiceFeignConfiguration;

@FeignClient(name = "loan", path = "/loan", configuration = LoanServiceFeignConfiguration.class)
public interface LoanFeignClient {

    @PostMapping("/api/v1/internal/balance")
    ResultData<LoanInfoResponse> getLoanBalance(LoanSimpleRequestDTO requestDTO);

    @PostMapping("/api/v1/internal/additional")
    ResultData<LoanAdditionalResponseDTO> getLoanAdditionalInfo(LoanSimpleRequestDTO requestDTO);

}
