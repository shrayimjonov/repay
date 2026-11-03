package uz.repay.card.service.business.adapter;

import uz.repay.card.model.dto.core.request.card.CardSaverParamDTO;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;

public interface CardSaveService {

    void saveCardByToken(CardSaverParamDTO<?> cardSaverDTO);

    void saveCardByPan(CardSaverParamDTO<?> cardSaverDTO);

    default SimpleResponse<Processing> getPC() {
        return new SimpleResponse<>(ResultState.NOT_IMPLEMENTED.getCode(), ResultState.NOT_IMPLEMENTED.getMessage());
    }
}
