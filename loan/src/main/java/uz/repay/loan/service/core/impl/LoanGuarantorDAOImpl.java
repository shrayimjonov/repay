package uz.repay.loan.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.loan.model.entity.LoanGuarantor;
import uz.repay.loan.repository.LoanGuarantorRepository;
import uz.repay.loan.service.core.LoanGuarantorDAO;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class LoanGuarantorDAOImpl implements LoanGuarantorDAO {
    private final LoanGuarantorRepository repository;

    @Override
    @Transactional
    public LoanGuarantor save(LoanGuarantor loanGuarantor) {
        log.info("Save/Update loan guarantor entity: {}", loanGuarantor);
        return repository.save(loanGuarantor);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LoanGuarantor> findByLoanIdAndGuarantorId(Long loanId, Long guarantorId) {
        log.info("Find loan guarantor by loanId: {} and guarantorId: {}", loanId, guarantorId);
        return repository.findByLoanIdAndGuarantorId(loanId, guarantorId);
    }
}
