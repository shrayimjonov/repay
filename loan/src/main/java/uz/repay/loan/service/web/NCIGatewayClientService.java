package uz.repay.loan.service.web;

import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoV3Response;
import uz.repay.loan.model.dto.nciBilling.response.ClientInfoResponse;
import uz.repay.loan.model.dto.nciBilling.response.DebtsListResponse;
import uz.repay.loan.model.dto.nciBilling.response.TransactionResponse;
import uz.repay.loan.model.dto.nciGateway.response.NCIBankDayResponse;
import uz.repay.loan.model.dto.nciGateway.response.NCILoanInfoRepayResponse;

import java.util.List;

public interface NCIGatewayClientService {
    SimpleResponse<ResultData<LoanInfoResponse>> getLoanInfoByLoanId(String loanId);

    ResultData<LoanInfoResponse> getLoanInfoByLoanIdWithRetry(String loanId);

    SimpleResponse<ClientInfoResponse> getClientInfoByLoanId(String loanId);

    SimpleResponse<DebtsListResponse> getDebtorsListByBranch(String branch);

    SimpleResponse<List<TransactionResponse>> getLoanTransactions(String loanId,
                                                                  String dateFrom);

    SimpleResponse<List<NCIBankDayResponse>> checkBankDay();

    SimpleResponse<ResultData<LoanInfoV3Response>> getLoanInfoV3(String loanId);

    SimpleResponse<ResultData<NCILoanInfoRepayResponse>> getLoanInfoRepayByLoanExtId(String loanExtId);
}
