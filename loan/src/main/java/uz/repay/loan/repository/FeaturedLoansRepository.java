package uz.repay.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.repay.loan.model.entity.FeaturedLoans;
import uz.repay.loan.model.entity.Loan;
import uz.repay.loan.model.entity.embedded.LoanFeaturePK;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeaturedLoansRepository extends JpaRepository<FeaturedLoans, LoanFeaturePK> {

    Optional<FeaturedLoans> findByLoanAndCreatedBy(Loan loan, String username);

    @Query("select fl from FeaturedLoans fl join fetch fl.loan where fl.createdBy=:username")
    List<FeaturedLoans> findAllByCreatedBy(String username);
}
