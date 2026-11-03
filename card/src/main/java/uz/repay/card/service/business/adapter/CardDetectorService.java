package uz.repay.card.service.business.adapter;

import uz.repay.card.model.dto.core.response.card.CardSaverResultDTO;
import uz.repay.card.model.entity.Loan;
import uz.repay.common.model.dto.response.core.SimpleResponse;


public interface CardDetectorService {

    SimpleResponse<CardSaverResultDTO<?>> getCardsByPersonCode(Loan loan);

    boolean isPaid();
    boolean isEnable();

}
