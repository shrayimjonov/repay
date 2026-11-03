package uz.repay.loan.service.business;

import uz.repay.common.model.dto.response.annotations.LoanId;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.loan.response.LoanClientInfoResponseDTO;

public interface LoanInfoService {
    ResultData<LoanClientInfoResponseDTO> getLoanClientInfoByExtId(@LoanId String loanExtId);
}
