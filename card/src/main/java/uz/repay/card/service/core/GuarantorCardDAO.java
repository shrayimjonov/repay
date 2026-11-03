package uz.repay.card.service.core;

import uz.repay.card.model.entity.GuarantorCard;

import java.util.Optional;

public interface GuarantorCardDAO {

    Optional<GuarantorCard> findByGuarantorAndCard(Long loanId, Long cardId);

    GuarantorCard save(GuarantorCard guarantorCard);
}
