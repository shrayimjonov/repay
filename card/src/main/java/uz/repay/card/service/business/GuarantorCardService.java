package uz.repay.card.service.business;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import uz.repay.card.model.dto.core.request.card.GuarantorCardInfoRequestDTO;
import uz.repay.card.model.dto.core.request.guarantor.GuarantorCardFinderRequestDTO;
import uz.repay.card.model.dto.core.request.loan.GuarantorCardToggleRequestDTO;
import uz.repay.card.model.dto.core.response.card.CardFinderResponseDTO;
import uz.repay.card.model.dto.core.response.card.CardInfoResponseDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

public interface GuarantorCardService {
    ResultData<CardInfoResponseDTO> getCardInfo(@NonNull GuarantorCardInfoRequestDTO requestDTO);

    ResultData<CoreError> deleteOrApproveCard(@NotNull GuarantorCardToggleRequestDTO dto);

    ResultData<CardFinderResponseDTO> findGuarantorCards(@NotNull GuarantorCardFinderRequestDTO requestDTO);

}
