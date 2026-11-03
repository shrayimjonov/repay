package uz.repay.card.service.core;

import uz.repay.card.model.dto.core.request.card.OTPRequestDTO;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.model.dto.master.response.MasterBalanceResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.entity.Card;
import uz.repay.card.repository.projection.HumoAutoDebitCardProjection;
import uz.repay.card.repository.projection.HumoLoanCardProjection;
import uz.repay.common.model.enums.Processing;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CardDAO {
    void create(OTPRequestDTO requestDTO, Processing processing);

    Card create(HumoCardInfoResponse humoInfo, Boolean isConfirmed);

    Card create(CardBaseResult result, Boolean isConfirmed, Processing pc);

    Optional<Card> findCardByPan(String card);

    void update(Card card);

    Card findById(Long id);

    List<Card> getAll(Boolean isActive);

    Optional<Card> findCardByToken(String token);

    Card create(Card preparedCard);

    List<Card> findAllByPC(Processing processing);

    Card create(MasterBalanceResponse result, boolean isConfirmed, Processing pc);

    Optional<Card> findCardByPanV2(String card);

    List<HumoLoanCardProjection> findHumoLoanCardsByLoanExtIds(List<String> loanExtIds);


    List<HumoAutoDebitCardProjection> findActiveAutoDebitHumoCardsByKafkaPartitions(
            Collection<Integer> partitions,
            int partitionCount
    );
}
