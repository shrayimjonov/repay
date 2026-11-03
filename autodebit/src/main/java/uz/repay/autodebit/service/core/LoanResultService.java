package uz.repay.autodebit.service.core;

import jakarta.validation.constraints.NotNull;
import uz.repay.autodebit.model.result.request.LoanResultRequestDTO;
import uz.repay.autodebit.model.result.response.LoanResultResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.UUID;

public interface LoanResultService {
    ResultData<LoanResultResponseDTO> getResultsByLoan(@NotNull LoanResultRequestDTO requestDTO);

    void save(String loan, String message, String sessionId, UUID workerId);
}
