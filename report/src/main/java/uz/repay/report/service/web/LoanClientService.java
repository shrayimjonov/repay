package uz.repay.report.service.web;

import uz.repay.common.model.dto.request.loan.params.LoanSearchParams;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.report.model.dto.debtLoan.response.DebtLoanResponse;
import uz.repay.report.model.dto.loan.response.LoanSimpleResponse;

import java.util.List;

public interface LoanClientService {

    ResultData<List<LoanSimpleResponse>> getAllLoans(LoanSearchParams params);

    ResultData<List<DebtLoanResponse>> getAllDebtLoans();
}
