package uz.repay.autodebit.service.core;

import lombok.NonNull;
import uz.repay.autodebit.model.core.response.AutoPayResult;
import uz.repay.autodebit.model.core.response.AutoPayResultV2;
import uz.repay.autodebit.model.core.response.PayResultResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

public interface PayResultService {
    void create(AutoPayResult s);

    void createV2(AutoPayResultV2 s);
    @Deprecated
    ResultData<PayResultResponseDTO> getResultsBySessionID(@NonNull String sessionId);
    @Deprecated
    ResultData<List<PayResultResponseDTO>> getResultsByLoanExt(@NonNull String loanExt);
}
