package uz.repay.loan.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoV3Response;
import uz.repay.loan.client.configuration.NCIGatewayClientConfiguration;
import uz.repay.loan.model.dto.nciBilling.request.LoanTranRequest;
import uz.repay.loan.model.dto.nciBilling.request.SimpleBranchRequest;
import uz.repay.loan.model.dto.nciBilling.response.ClientInfoResponse;
import uz.repay.loan.model.dto.nciBilling.response.DebtsListResponse;
import uz.repay.loan.model.dto.nciBilling.response.TransactionResponse;
import uz.repay.loan.model.dto.nciGateway.request.NCIGatewayClientInfoRequest;
import uz.repay.loan.model.dto.nciGateway.request.NCIGatewayLoanInfoRequest;
import uz.repay.loan.model.dto.nciGateway.response.NCIBankDayResponse;
import uz.repay.loan.model.dto.nciGateway.response.NCILoanInfoRepayResponse;

import java.util.List;

@FeignClient(name = "nci-gateway", url = "${external.nci-gateway.url}",
        configuration = NCIGatewayClientConfiguration.class)
public interface NCIGatewayFeignClient {

    @PostMapping("/loan/info")
    ResultData<LoanInfoResponse> getLoanInfoByLoanId(@Valid NCIGatewayLoanInfoRequest request);

    @PostMapping("/client/byLoan")
    ClientInfoResponse getClientInfoByLoanId(@Valid NCIGatewayClientInfoRequest request);

    @PostMapping("/loan/debts")
    DebtsListResponse getDebtorsListByBranch(@Valid SimpleBranchRequest request);

    @PostMapping("/loan/transactions")
    List<TransactionResponse> getLoanTransactions(@Valid LoanTranRequest request);

    @GetMapping("/day/v2")
    List<NCIBankDayResponse> checkBankDay();

    @PostMapping("/loan/info/v3")
    ResultData<LoanInfoV3Response> getLoanInfoV3(NCIGatewayLoanInfoRequest request);

    @PostMapping("/loan/info/repay")
    ResultData<NCILoanInfoRepayResponse> getLoanInfoRepay(@Valid NCIGatewayLoanInfoRequest request);


}
