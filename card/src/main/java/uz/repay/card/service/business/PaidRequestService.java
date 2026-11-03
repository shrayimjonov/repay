package uz.repay.card.service.business;

import jakarta.validation.constraints.NotNull;
import uz.repay.card.model.dto.paidRequest.response.PaidRequestResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

public interface PaidRequestService {

    ResultData<List<PaidRequestResponseDTO>> findRequestByLoanExtId(@NotNull String loanExtId);
}
