package uz.repay.loan.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.nodir.repayautoconfigure.utils.CacheUtils;
import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.loan.model.entity.DebtLoan;
import uz.repay.loan.model.entity.domain.DebtLoanReport;
import uz.repay.loan.repository.DebtLoanRepository;
import uz.repay.loan.service.core.DebtLoanDAO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebtLoanDAOImpl implements DebtLoanDAO {
    private final DebtLoanRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Page<DebtLoan> findAll(Specification<DebtLoan> specification, Pageable pageable) {
        return repository
                .findAll(specification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DebtLoan> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    //@Cacheable(cacheNames = CacheUtils.DEBT_LOAN, sync = true)
    public List<DebtLoan> findAll() {
        log.info("Get All Debt Loans list...");
        return repository.findAll();
    }

    @Override
    @Transactional
    // @CacheEvict(cacheNames = CacheUtils.DEBT_LOAN, allEntries = true)
    public void deleteAll() {
        log.info("Delete All Debt Loans from Database...");
        repository.deleteAllInBatch();
    }

    @Override
    @Transactional
    // @CacheEvict(cacheNames = CacheUtils.DEBT_LOAN, allEntries = true)
    public void saveAll(List<DebtLoan> debtLoanList) {
        log.info("Save All Debt Loans from Database...");
        repository.saveAllAndFlush(debtLoanList);
    }

    @Override
    @Transactional
    public void replaceAll(List<DebtLoan> debtLoanList) {
        log.info("Replacing all Debt Loans in a single transaction. New count -> {}",
                debtLoanList == null ? 0 : debtLoanList.size());
        repository.deleteAllInBatch();
        if (debtLoanList != null && !debtLoanList.isEmpty()) {
            repository.saveAllAndFlush(debtLoanList);
        }
    }

    @Override
    //  @CacheEvict(cacheNames = CacheUtils.DEBT_LOAN, allEntries = true)
    public void evictDebtLoanCache() {
        log.info("Cache {} all entries is evicted!", CacheUtils.DEBT_LOAN);
    }


    @Override
    @Transactional(readOnly = true)
    public DebtLoan findById(Long id) {
        log.info("Find debtLoan by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Debt loan with id: %d not found".formatted(id)));
    }

    @Override
    @Transactional
    // @CacheEvict(cacheNames = CacheUtils.DEBT_LOAN, allEntries = true)
    public void deleteById(Long id) {
        log.info("Delete debtLoan by id: {}", id);
        repository.deleteById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<DebtLoanReport> findDailyReport() {
        log.info("Find total daily report by branch");
        return repository.findAllByBranchAndCreatedDate();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DebtLoan> findByLoanExtId(String loanExtId) {
        log.info("Find debt loan by loan ext id: {}", loanExtId);
        return repository.findByLoanExtId(loanExtId);
    }
}
