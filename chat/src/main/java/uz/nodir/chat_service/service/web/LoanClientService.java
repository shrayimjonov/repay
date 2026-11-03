package uz.nodir.chat_service.service.web;

import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;

import java.util.List;

public interface LoanClientService {

    SimpleResponse<ResultData<List<String>>> takeLoanFeaturedCreators(String loanExtId);

    SimpleResponse<ResultData<LoanInfoResponse>> getLoanBalance(LoanSimpleRequestDTO requestDTO);
}
