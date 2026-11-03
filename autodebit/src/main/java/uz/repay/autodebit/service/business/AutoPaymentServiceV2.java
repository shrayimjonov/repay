package uz.repay.autodebit.service.business;

import uz.repay.autodebit.model.core.request.AutoPaymentSwitcherDTO;
import uz.repay.autodebit.model.core.request.AutodebitTriggerType;
import uz.repay.autodebit.model.core.response.LoanProcessResult;
import uz.repay.autodebit.model.core.response.WorkersResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

public interface AutoPaymentServiceV2 {
    void switchAP(AutoPaymentSwitcherDTO dto);

    ResultData<Boolean> getStatus();

    ResultData<List<WorkersResponseDTO>> getWorkers();

    boolean processLoan(String loanExternalId, String branchId);

    LoanProcessResult processLoanWithResult(String loanExternalId, String branchId, AutodebitTriggerType triggerType);

    LoanProcessResult processLoanDirect(String loanExternalId, String branchId);
}
