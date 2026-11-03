package uz.repay.card.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.card.model.entity.GuarantorCard;
import uz.repay.card.repository.GuarantorCardRepository;
import uz.repay.card.service.core.GuarantorCardDAO;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class GuarantorCardDAOImpl implements GuarantorCardDAO {
    private final GuarantorCardRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<GuarantorCard> findByGuarantorAndCard(Long loanId, Long cardId) {
        log.info("Search loanCard by loanId: {} and cardId: {}", loanId, cardId);
        return repository.findByGuarantorIdAndCardId(loanId, cardId);
    }

    @Override
    public GuarantorCard save(GuarantorCard guarantorCard) {
        log.info("Save/Update guarantorCard entity: {}", guarantorCard);

        var result = repository.save(guarantorCard);

        log.info("Saved entity id: {}", guarantorCard.getId());
        return result;
    }
}
