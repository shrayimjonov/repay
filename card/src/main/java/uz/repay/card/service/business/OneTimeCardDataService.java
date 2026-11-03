package uz.repay.card.service.business;

import jakarta.validation.constraints.NotNull;
import uz.repay.card.model.dto.core.request.card.OTPRequestDTO;
import uz.repay.card.model.dto.core.request.card.VerifyRequestDTO;
import uz.repay.card.model.dto.core.response.card.OTPResponseDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.common.model.dto.response.core.ResultData;

public interface OneTimeCardDataService {
    ResultData<OTPResponseDTO> verify(@NotNull OTPRequestDTO requestDTO);

    ResultData<LoanCardsResult> confirmNotFamiliar(@NotNull VerifyRequestDTO requestDTO);

    ResultData<LoanCardsResult> confirmFamiliar(@NotNull VerifyRequestDTO requestDTO);

}
