package uz.repay.loan.service.business;

import jakarta.validation.constraints.NotNull;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.guarantor.request.GuarantorAddRequestDTO;
import uz.repay.loan.model.dto.core.guarantor.request.GuarantorToggleRequestDTO;

public interface GuarantorService {
    ResultData<CoreError> addGuarantorToLoan(@NotNull GuarantorAddRequestDTO requestDTO);

    ResultData<CoreError> deleteOrApproveGuarantor(@NotNull GuarantorToggleRequestDTO requestDTO);
}
