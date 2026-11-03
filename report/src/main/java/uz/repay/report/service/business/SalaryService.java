package uz.repay.report.service.business;

import lombok.NonNull;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.InpsInfo;

public interface SalaryService {
    void generateSalaryReport(@NonNull ResultData<@NonNull InpsInfo> request);
}
