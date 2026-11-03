package uz.repay.card.service.business;

import uz.repay.card.model.dto.core.request.autopay.request.AutoPayPushRequestDTO;
import uz.repay.common.model.dto.request.kafka.CardCreditEventDTO;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.CardType;

import java.time.LocalDateTime;

public interface CardBalanceAlertService {
    void pushToLoansWithAlertedCard(AutoPayPushRequestDTO requestDTO, LocalDateTime alertedDateTime);

    void pushToLoansWithAlertedCard(AutoPayPushRequestDTO requestDTO);

    void alertedFromDavrMobile(CardCreditEventDTO eventDTO);

    void debitFromCard(String card, Long amount, AddType addType, CardType cardType);

}
