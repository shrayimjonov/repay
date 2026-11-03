package uz.repay.loan.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.nodir.repayautoconfigure.utils.CacheUtils;
import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.loan.model.entity.Loan;
import uz.repay.loan.repository.LoanRepository;
import uz.repay.loan.service.core.LoanDAO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanDAOImpl implements LoanDAO {
    private final LoanRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Page<Loan> findAll(Specification<Loan> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    @Override
    @Transactional
//    @Caching(
//            evict = {
//                    @CacheEvict(cacheNames = CacheUtils.LOAN, key = "#loan.id"),
//                    @CacheEvict(cacheNames = CacheUtils.LOAN, key = "#loan.extId")
//            }
//    )
    public Loan save(Loan loan) {
        log.info("Save loan: {}", loan);
        return repository.save(loan);
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable(
//            cacheNames = CacheUtils.LOAN,
//            key = "#id",
//            unless = "#result instanceof T(java.lang.Exception)",
//            sync = true)
    public Loan findById(Long id) {
        log.info("Find Loan by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loan with id: %d not found".formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable(
//            cacheNames = CacheUtils.LOAN,
//            key = "#loanExtId",
//            unless = "#result instanceof T(java.lang.Exception)",
//            sync = true
//    )
    public Loan findByExtId(String loanExtId) {
        log.info("Find Loan by extId: {}", loanExtId);
        return repository.findByExtIdAndIsDeletedV2(loanExtId, false)
                .orElseThrow(() -> new EntityNotFoundException("Loan with extId: %s not found".formatted(loanExtId)));
    }


    @Override
    @Transactional(readOnly = true)
    public List<Loan> findAll(Specification<Loan> specification) {
        return repository.findAll(specification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Loan> getOptionalByExtId(String loanExtId) {
        return repository.findByExtIdAndIsDeleted(loanExtId, false);
    }

    @Override
    @CacheEvict(cacheNames = CacheUtils.LOAN, allEntries = true)
    public void cacheEvict() {
        log.info("Cache {} all entries is evicted!", CacheUtils.LOAN);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findAllNotDeleted() {
        log.info("Find all not deleted loans list");
        return repository.findAllByIsDeleted(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Loan> findByExtIdV2(String loanExtId) {
        return repository.findByExtId(loanExtId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findAllIsInsolvents() {
        log.info("Find all is insolvent loans");
        return repository.findAllByIsInsolventAndIsDeleted(true, false);
    }
}
