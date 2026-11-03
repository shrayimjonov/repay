package uz.repay.card.service.web;


import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;


public interface LoanService {

    SimpleResponse<ResultData<CoreError>> addLoan(String loanExtId);
}
