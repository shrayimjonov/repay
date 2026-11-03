package uz.repay.loan.service.business;

import jakarta.validation.constraints.NotNull;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

public interface LoanModifyService {

    ResultData<CoreError> add(@NotNull LoanSimpleRequestDTO requestDTO);

    ResultData<CoreError> addWithoutBranchValidation(@NotNull LoanSimpleRequestDTO requestDTO);

}
