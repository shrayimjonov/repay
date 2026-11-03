package uz.repay.payment.service.web;

import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;

public interface LoanFeignService {

    SimpleResponse<ResultData<LoanInfoResponse>> getLoanBalance(String loanExtId);

    ResultData<LoanAdditionalResponseDTO> getLoanAdditionalInfo(String loanExtId);

}
