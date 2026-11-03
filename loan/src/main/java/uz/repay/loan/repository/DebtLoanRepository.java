package uz.repay.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.repay.loan.model.entity.DebtLoan;
import uz.repay.loan.model.entity.domain.DebtLoanReport;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebtLoanRepository extends JpaRepository<DebtLoan, Long>, JpaSpecificationExecutor<DebtLoan> {

    @Query(value = "select branch, count(*), sum(recommended) as debt_amount, created_date  from debt_loan dl \n" +
            "group by branch, created_date", nativeQuery = true)
    List<DebtLoanReport> findAllByBranchAndCreatedDate();

    Optional<DebtLoan> findByLoanExtId(String loanExtId);
}
