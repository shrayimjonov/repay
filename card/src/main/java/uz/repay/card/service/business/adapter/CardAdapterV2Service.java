package uz.repay.card.service.business.adapter;

import jakarta.validation.constraints.NotNull;
import uz.repay.card.model.dto.adapter.CardBalanceFetchRequest;
import uz.repay.card.model.dto.core.response.card.CardShortInfoDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.card.model.enums.PC;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;

import java.util.List;

public sealed interface CardAdapterV2Service permits HuMoInfoAdapterV2Service, UzCardInfoAdapterV2Service,
        AutoPayInfoAdapterV2Service, VisaInfoAdapterV2Service, MasterInfoAdapterV2Service {
    SimpleResponse<List<LoanCardsResult>> getCardsBalance(@NotNull List<CardBalanceFetchRequest> cardsList);

    SimpleResponse<CardShortInfoDTO> takeP2PInfo(@NotNull String pan);

    SimpleResponse<LoanCardsResult> getOneCardBalance(@NotNull CardBalanceFetchRequest request);

    default SimpleResponse<Processing> getPC() {
        return new SimpleResponse<>(501, ResultState.NOT_IMPLEMENTED.getMessage());
    }


}
