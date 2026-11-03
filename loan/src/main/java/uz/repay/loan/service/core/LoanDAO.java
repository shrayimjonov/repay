package uz.repay.loan.service.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import uz.repay.loan.model.entity.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanDAO {
    Page<Loan> findAll(Specification<Loan> specification, Pageable pageable);

    Loan save(Loan loan);

    Loan findById(Long id);

    Loan findByExtId(String loanExtId);

    List<Loan> findAll(Specification<Loan> specification);

    List<Loan> findAll();

    Optional<Loan> getOptionalByExtId(String loanExtId);

    void cacheEvict();

    List<Loan> findAllNotDeleted();

    Optional<Loan> findByExtIdV2(String loanExtId);

    List<Loan> findAllIsInsolvents();
}
