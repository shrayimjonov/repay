package uz.repay.loan.service.business;

import jakarta.validation.constraints.NotNull;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.InpsInfo;

public interface ClientIncomeService {
    ResultData<InpsInfo> checkClientIncomeByPersonCode(@NotNull String personCode);
}
