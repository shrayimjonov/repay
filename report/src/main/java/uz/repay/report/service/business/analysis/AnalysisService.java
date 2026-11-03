package uz.repay.report.service.business.analysis;

import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.report.model.entity.MostDebitUsers;

import java.util.List;

public interface AnalysisService {

    ResultData<List<MostDebitUsers>> mostDebitUsers(int limit);
}
