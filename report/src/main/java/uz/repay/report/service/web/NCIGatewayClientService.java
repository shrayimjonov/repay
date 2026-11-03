package uz.repay.report.service.web;

import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanGraphResponse;

import java.util.List;

public interface NCIGatewayClientService {
    SimpleResponse<ResultData<List<LoanGraphResponse>>> getLoanGraph(String loanExtId);

}