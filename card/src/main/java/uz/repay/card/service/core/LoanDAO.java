package uz.repay.card.service.core;

import uz.repay.card.model.entity.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanDAO {

    Loan findById(Long id);

    Optional<Loan> findOptionalById(Long id);

    Loan findLoanByExtId(String extId);

    List<Loan> findAllNonDeleted();

    List<Loan> findByExtIdsAndNotDeleted(List<String> ids);

    Optional<Loan> findLoanOptionalByExtId(String extId);

}
