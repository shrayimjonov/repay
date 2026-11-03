package uz.repay.autodebit.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import uz.repay.autodebit.client.configuration.LoanServiceFeignConfiguration;
import uz.repay.autodebit.model.loan.response.DebtLoansPageResponseDTO;
import uz.repay.autodebit.model.loan.response.DebtLoansResult;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoV3Response;
import uz.repay.common.model.dto.response.loan.LoanShortV2ResponseDTO;

import java.util.List;

@FeignClient(name = "loan", path = "/loan", configuration = LoanServiceFeignConfiguration.class)
public interface LoanFeignClient {
    @GetMapping("/api/v1/internal/debtLoans")
    ResultData<List<DebtLoansResult>> getDebtLoans();

    @PostMapping("/api/v1/internal/balance")
    ResultData<LoanInfoResponse> getLoanBalance(LoanSimpleRequestDTO requestDTO);

    @DeleteMapping("/api/v1/internal/debtLoan")
    ResultData<Boolean> removeFromDebtLoan(@RequestParam("id") Long id);

    @PostMapping("/api/v1/internal/balance/v3")
    ResultData<LoanInfoV3Response> getLoanBalanceV3(LoanSimpleRequestDTO requestDTO);

    @GetMapping("/api/v1/internal/insolvents")
    ResultData<List<LoanShortV2ResponseDTO>> getInsolventLoans();

    @DeleteMapping("/api/v1/internal/debtLoanByLoanExtId")
    ResultData<CoreError> removeFromDebtLoanByLoanExtId(@RequestParam("id") String id);

    @PostMapping("/api/v1/internal/additional")
    ResultData<LoanAdditionalResponseDTO> getLoanAdditionalInfo(LoanSimpleRequestDTO requestDTO);

    @GetMapping("/api/v2/internal/debtLoans")
    ResultData<byte[]> getDebtLoansV2();

    @GetMapping("/api/v2/internal/debtLoans/page")
    ResponseEntity<ResultData<DebtLoansPageResponseDTO>> getDebtLoansPage(
            @RequestParam("limit") int limit,
            @RequestParam("pageNumber") long pageNumber
    );
}
