package uz.repay.loan.service.core;

import uz.repay.loan.model.entity.FeaturedLoans;
import uz.repay.loan.model.entity.Loan;
import uz.repay.loan.model.entity.embedded.LoanFeaturePK;

import java.util.List;
import java.util.Optional;


public interface FeaturedLoansDAO {
    FeaturedLoans create(FeaturedLoans featuredLoan);

    Optional<FeaturedLoans> findByLoanAndUser(Loan loan, String username);

    void deleteById(LoanFeaturePK id);

    List<FeaturedLoans> findUserFeaturedLoans(String username);
}
