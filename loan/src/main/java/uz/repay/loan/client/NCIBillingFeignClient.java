package uz.repay.loan.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.loan.client.configuration.NCIBillingClientConfiguration;
import uz.repay.loan.model.dto.nciBilling.request.LoanRepayRequest;
import uz.repay.loan.model.dto.nciBilling.response.ProblemResponse;
import uz.repay.loan.model.dto.nciBilling.request.LoanTranRequest;
import uz.repay.loan.model.dto.nciBilling.request.SimpleBranchRequest;
import uz.repay.loan.model.dto.nciBilling.request.SimpleLoanRequest;
import uz.repay.loan.model.dto.nciBilling.response.ClientInfoResponse;
import uz.repay.loan.model.dto.nciBilling.response.DebtsListResponse;
import uz.repay.loan.model.dto.nciBilling.response.TransactionResponse;

import java.util.List;

@FeignClient(name = "nciGate", path = "/nci", url = "${external.nci-billing.url}", configuration = NCIBillingClientConfiguration.class)
public interface NCIBillingFeignClient {

    @Deprecated
    @PostMapping("/v2/info/loan")
    ResultData<LoanInfoResponse> getLoanInfoByLoanId(@Valid SimpleLoanRequest request);

    @Deprecated
    @PostMapping("/v2/info/client")
    ResultData<ClientInfoResponse> getClientInfoByLoanId(@Valid SimpleLoanRequest request);

    @PostMapping("/v2/info/debts")
    @Deprecated
    ResultData<DebtsListResponse> getDebtorsListByBranch(@Valid SimpleBranchRequest request);

    @PostMapping("/v2/info/transactions")
    @Deprecated
    ResultData<List<TransactionResponse>> getLoanTransactions(@Valid LoanTranRequest request);

    @PostMapping("/core/repay/v2")
    ProblemResponse repayV2(LoanRepayRequest request);

}
