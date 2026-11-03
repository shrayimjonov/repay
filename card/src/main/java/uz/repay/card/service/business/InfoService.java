package uz.repay.card.service.business;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import uz.repay.card.model.dto.core.request.card.GetCardByTokenRequest;
import uz.repay.card.model.dto.core.response.card.GetCardByTokenResponse;
import uz.repay.card.model.dto.core.response.card.CardShortInfoDTO;
import uz.repay.common.model.dto.request.card.request.CardFullInfoRequestDTO;
import uz.repay.common.model.dto.request.card.response.CardFullInfoResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

public interface InfoService {
    ResultData<CardFullInfoResponseDTO> getCardByPan(@NotNull CardFullInfoRequestDTO requestDTO);

    ResultData<Boolean> deActivateCard(@NotNull Long id);

    ResultData<CardShortInfoDTO> takeP2PInfo(@NotNull String pan);

    GetCardByTokenResponse getCardByToken(@Valid GetCardByTokenRequest requestDTO);
}
