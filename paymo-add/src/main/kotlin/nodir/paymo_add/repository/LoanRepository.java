package nodir.paymo_add.repository;

import nodir.paymo_add.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = "select * FROM marketplace.loan l WHERE l.external_id =:loanId", nativeQuery = true)
    Loan findLoanInfo (String loanId);
}
