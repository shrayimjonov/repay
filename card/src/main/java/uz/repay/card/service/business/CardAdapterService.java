package uz.repay.card.service.business;

import lombok.NonNull;
import uz.repay.card.model.dto.core.response.card.CardShortInfoDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;

import java.util.List;
import java.util.Map;

public sealed interface CardAdapterService permits HumoInfoAdapterService, UzCardInfoAdapterService, VisaDavrInfoAdapterService,
        AutoPayInfoAdapterService, MasterInfoAdapterService {
    List<LoanCardsResult> getCardsBalance(Map<Long, String> cardList);

    LoanCardsResult getOneCardBalance(String card);

    CardShortInfoDTO takeP2PInfo(@NonNull String pan);

}
