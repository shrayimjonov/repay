package uz.repay.loan.service.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import uz.repay.loan.model.entity.DebtLoan;
import uz.repay.loan.model.entity.domain.DebtLoanReport;

import java.util.List;
import java.util.Optional;

public interface DebtLoanDAO {
    Page<DebtLoan> findAll(Specification<DebtLoan> specification, Pageable pageable);

    Page<DebtLoan> findAll(Pageable pageable);

    List<DebtLoan> findAll();

    void deleteAll();

    void saveAll(List<DebtLoan> debtLoanList);

    /**
     * Atomically replace the whole debt_loan table: delete all rows and insert the new list
     * within a single transaction. If the insert fails, the delete is rolled back and the
     * previous data is preserved (no empty-table window).
     */
    void replaceAll(List<DebtLoan> debtLoanList);

    void evictDebtLoanCache();

    DebtLoan findById(Long id);

    void deleteById(Long id);

    List<DebtLoanReport> findDailyReport();

    Optional<DebtLoan> findByLoanExtId(String loanExtId);
}
