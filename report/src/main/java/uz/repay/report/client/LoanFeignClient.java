package uz.repay.report.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.report.client.configuration.LoanClientConfiguration;
import uz.repay.report.model.dto.debtLoan.response.DebtLoanResponse;
import uz.repay.report.model.dto.loan.request.LoanFilterRequest;
import uz.repay.report.model.dto.loan.response.LoanSimpleResponse;

import java.util.List;

@FeignClient(name = "loan",  configuration = LoanClientConfiguration.class)
public interface LoanFeignClient {

    @PostMapping("/loan/api/v1/internal/getLoans")
    ResultData<List<LoanSimpleResponse>> getAllLoans(LoanFilterRequest request);

    @GetMapping("/loan/api/v1/internal/debtLoans")
    ResultData<List<DebtLoanResponse>> getAllDebtLoans();
}
