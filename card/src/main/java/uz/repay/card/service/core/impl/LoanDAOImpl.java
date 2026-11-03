package uz.repay.card.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.repository.LoanRepository;
import uz.repay.card.service.core.LoanDAO;
import uz.repay.common.exception.EntityNotFoundException;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanDAOImpl implements LoanDAO {
    private final LoanRepository repository;

    @Override
    @Transactional(readOnly = true)
    //   @Cacheable(cacheManager = "cardHazelCastCacheManager", value = CacheUtils.LOAN_CARD, key = "#p0")
    public Loan findById(Long id) {
        log.info("Get loan by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loan with id: %d not found in database".formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Loan> findOptionalById(Long id) {
        log.info("Search loan by id: {}", id);
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Loan findLoanByExtId(String extId) {
        log.info("Search loan by extId: {}", extId);
        return repository.findByExtId(extId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Loan: %s not found in database!".formatted(extId)));
    }


    @Override
    @Transactional(readOnly = true)
    public List<Loan> findAllNonDeleted() {
        return repository.findAllByIsDeleted(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findByExtIdsAndNotDeleted(List<String> ids) {
        return repository.findByIdsAndIsDeleted(ids, false);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Loan> findLoanOptionalByExtId(String extId) {
        log.info("Search loan by extId: {}", extId);
        return repository.findByExtId(extId);
    }
}
