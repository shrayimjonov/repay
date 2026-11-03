package uz.repay.card.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uz.repay.card.model.entity.Card;
import uz.repay.card.model.entity.DeletedCards;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.model.entity.LoanCard;
import uz.repay.card.model.entity.pk.LoanCardKey;
import uz.repay.card.model.enums.CardAction;
import uz.repay.card.repository.DeletedCardsRepository;
import uz.repay.card.repository.LoanCardRepository;
import uz.repay.card.service.core.LoanCardDAO;
import uz.repay.common.exception.CardException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.utils.HttpServletUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanCardDAOImpl implements LoanCardDAO {
    private final LoanCardRepository repository;
    private final DeletedCardsRepository deletedCardsRepository;
    private final RedissonClient redissonClient;
    private final static Integer LOCK_WAIT_TIME = 20;
    private final static Integer LOCK_TTL = 30;

    @Override
    @Transactional(readOnly = true)
    public Optional<LoanCard> findByLoanAndCard(Long loanId, Long cardId) {
        log.info("Search loanCard by loanId: {} and cardId: {}", loanId, cardId);
        return repository.findByLoanIdAndCardId(loanId, cardId);
    }

    @Override
    // @CacheEvict(cacheManager = "cardHazelCastCacheManager", value = CacheUtils.LOAN_CARD, key = "#loan.id")
    @Transactional
    public Loan create(Loan loan, Card card, Payer payer, AddType addType) {
        log.info("Creating loan card for Loan: {} with card: {}", loan.getExtId(), card.getMaskedPan());

        var preparedEntity = new LoanCard(
                new LoanCardKey(
                        loan.getId(), card.getId()
                ),
                loan,
                card,
                payer,
                false,
                HttpServletUtils.getRequestIPAddress(),
                addType
        );
        log.info("Prepared entity: {}", preparedEntity);

        return repository.save(preparedEntity).getLoan();
    }

    @Override
    //@CacheEvict(cacheManager = "cardHazelCastCacheManager", value = CacheUtils.LOAN_CARD, key = "#loan.id")
    @Transactional
    @Deprecated
    public void deleteOrApprove(Loan loan, Long cardId, Boolean isDelete) {
        var loanCardOptional =
                loan.getLoanCards()
                        .stream()
                        .filter(l -> l.getLoan().equals(loan) && l.getCard().getId().equals(cardId))
                        .findFirst();

        if (loanCardOptional.isEmpty()) {
            log.info("Loan Card: #{} not found in Loan: {}", cardId, loan.getExtId());
            throw new CardException("Card: #%d not found in Loan: %s".formatted(cardId, loan.getExtId()));
        } else {
            log.info("Loan card found, can continue...");
            var loanCard = loanCardOptional.get();
            loanCard.setIsDeleted(isDelete);
            repository.save(loanCard);

            saveToHistory(loan, cardId, isDelete);
        }
    }

    private void saveToHistory(Loan loan, Long cardId, Boolean isDelete) {
        var action = isDelete ? CardAction.DELETED : CardAction.APPROVED;

        var deleteEntity = new DeletedCards(
                cardId,
                loan.getId(),
                action
        );
        deletedCardsRepository.save(deleteEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanCard> findByCardToken(String token) {
        log.info("Search loanCard by card token: {}", token);
        return repository.findByCardToken(token);
    }


    @Override
    @Transactional
    public LoanCard save(LoanCard loanCard) {
        log.info("Save/Update loanCard entity: {}", loanCard);

        var result = repository.save(loanCard);

        log.info("Saved entity id: {}", loanCard.getId());
        return result;
    }


    @Override
    @Transactional
    public LoanCard createV2(Loan loan, Card card, Payer payer, AddType addType, Boolean isConfirmed) {
        log.info("Creating loan card v2 for Loan: {} with card: {}", loan.getExtId(), card.getMaskedPan());

        var loanId = loan.getId();
        var cardId = card.getId();
        RLock lock = redissonClient.getLock("loan-card-save:%d:%d".formatted(loanId, cardId));

        boolean isLocked = false;
        boolean unlockRegistered = false;
        try {
            isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_TTL, TimeUnit.SECONDS);

            if (!isLocked){
                throw new GeneralErrorException("LoanCard is locked: loanId=%d, cardId=%d".formatted(loanId, cardId));
            }

            unlockRegistered = registerUnlockAfterTransaction(lock);
            return saveOrUpdateLoanCard(loan, card, payer, addType, isConfirmed);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GeneralErrorException("Ошибка блокировки: %s".formatted(e.getMessage()));
        } finally {
            if (isLocked && !unlockRegistered)
                unlock(lock);
        }
    }

    private boolean registerUnlockAfterTransaction(RLock lock) {
        if (!TransactionSynchronizationManager.isSynchronizationActive())
            return false;

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        unlock(lock);
                    }
                }
        );
        return true;
    }

    private void unlock(RLock lock) {
        if (lock.isHeldByCurrentThread()){
            lock.unlock();
        }
    }

    private LoanCard saveOrUpdateLoanCard(Loan loan, Card card, Payer payer, AddType addType, Boolean isConfirmed) {
        var loanId = loan.getId();
        var cardId = card.getId();
        var ipAddress = HttpServletUtils.getRequestIPAddress();

        var loanCardOptional = repository.findByLoanIdAndCardId(loanId, cardId);

        if (loanCardOptional.isPresent()) {
            log.info("LoanCard already exists and will be updated. loanId: {}, cardId: {}", loanId, cardId);
            var loanCard = loanCardOptional.get();

            loanCard.setLoan(loan);
            loanCard.setCard(card);
            loanCard.setPayer(payer);
            loanCard.setIsDeleted(false);
            loanCard.setIpAddress(ipAddress);
            loanCard.setAddType(addType);
            loanCard.setIsConfirmed(isConfirmed);

            return repository.save(loanCard);
        }

        var preparedEntity = new LoanCard(
                new LoanCardKey(
                        loanId, cardId
                ),
                loan,
                card,
                payer,
                false,
                ipAddress,
                addType,
                isConfirmed
        );
        log.info("Prepared entity: {}", preparedEntity);

        return repository.save(preparedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanCard> findByCardPan(String pan) {
        log.info("Search loanCard by card pan: {}", pan);
        return repository.findByCardPan(pan);
    }
}
