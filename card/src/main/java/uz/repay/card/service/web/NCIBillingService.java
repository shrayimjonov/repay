package uz.repay.card.service.web;


import uz.repay.card.model.dto.nciBilling.response.NCICardsByPersonCodeResponse;
import uz.repay.card.model.dto.nciBilling.response.NCIClientInfoByPersonCode;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;

import java.util.List;

public interface NCIBillingService {

    ResultData<List<NCICardsByPersonCodeResponse>> getCardsByPersonCode(String personCode);

    ResultData<LoanInfoResponse> getLoanInfo(String loanExtId);

    SimpleResponse<ResultData<List<NCIClientInfoByPersonCode>>> getClientInfoByPersonCode(String personCode);
}
