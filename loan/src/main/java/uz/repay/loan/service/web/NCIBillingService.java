package uz.repay.loan.service.web;

import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.loan.model.dto.nciBilling.response.ClientInfoResponse;
import uz.repay.loan.model.dto.nciBilling.response.DebtsListResponse;
import uz.repay.loan.model.dto.nciBilling.response.ProblemResponse;
import uz.repay.loan.model.dto.nciBilling.response.TransactionResponse;

import java.util.List;

public interface NCIBillingService {

    @Deprecated
    ResultData<LoanInfoResponse> getLoanInfoByLoanId(String loanId);

    @Deprecated
    ResultData<ClientInfoResponse> getClientInfoByLoanId(String loanId);

    @Deprecated
    ResultData<DebtsListResponse> getDebtorsListByBranch(String branch);

    @Deprecated
    ResultData<List<TransactionResponse>> getLoanTransactions(String loanId,
                                                              String dateFrom);

    ResultData<ProblemResponse> loanRepay(String loanId, Long amount);
}
