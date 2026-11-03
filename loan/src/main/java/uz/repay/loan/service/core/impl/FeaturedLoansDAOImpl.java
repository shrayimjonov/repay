package uz.repay.loan.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.loan.model.entity.FeaturedLoans;
import uz.repay.loan.model.entity.Loan;
import uz.repay.loan.model.entity.embedded.LoanFeaturePK;
import uz.repay.loan.repository.FeaturedLoansRepository;
import uz.repay.loan.service.core.FeaturedLoansDAO;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeaturedLoansDAOImpl implements FeaturedLoansDAO {
    private final FeaturedLoansRepository repository;

    @Override
    @Transactional
    public FeaturedLoans create(FeaturedLoans featuredLoan) {
        log.info("Save/Update featured loan entity: {}", featuredLoan);
        return repository.save(featuredLoan);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FeaturedLoans> findByLoanAndUser(Loan loan, String username) {
        log.info("Find featured loan by loan: {} and username: {}", loan.getExtId(), username);
        return repository.findByLoanAndCreatedBy(loan, username);
    }

    @Override
    @Transactional
    public void deleteById(LoanFeaturePK id) {
        log.info("Delete featured loan by id: {}", id);
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeaturedLoans> findUserFeaturedLoans(String username) {
        log.info("Find featured loans by username: {}", username);
        return repository.findAllByCreatedBy(username);
    }
}
