package uz.repay.autodebit.service.web;

import org.springframework.http.ResponseEntity;
import uz.repay.autodebit.model.loan.response.DebtLoansPageResponseDTO;
import uz.repay.autodebit.model.loan.response.DebtLoansResult;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoV3Response;
import uz.repay.common.model.dto.response.loan.LoanShortV2ResponseDTO;

import java.util.List;

public interface LoanFeignService {

    ResultData<List<DebtLoansResult>> getAllDebtLoans();

    ResultData<LoanInfoResponse> getLoanBalance(String loanExtId);

    SimpleResponse<ResultData<Boolean>> removeDebtLoanById(Long id);

    ResultData<LoanInfoV3Response> getLoanBalanceV3(String loanExtId);

    ResultData<List<LoanShortV2ResponseDTO>> getInsolventLoans();

    SimpleResponse<ResultData<Boolean>> removeDebtLoanByLoanExtId(String loanExtId);

    ResultData<LoanAdditionalResponseDTO> getLoanAdditionalInfo(String loanExtId);

    ResultData<List<DebtLoansResult>> getAllDebtLoans2();

    ResultData<byte[]> getAllDebtLoansV2();

    ResponseEntity<ResultData<DebtLoansPageResponseDTO>> getAllDebtLoansPageV2(int limit, long pageNumber);
}
