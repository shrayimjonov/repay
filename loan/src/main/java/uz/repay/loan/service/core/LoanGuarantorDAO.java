package uz.repay.loan.service.core;

import uz.repay.loan.model.entity.LoanGuarantor;

import java.util.Optional;

public interface LoanGuarantorDAO {
    LoanGuarantor save(LoanGuarantor loanGuarantor);

    Optional<LoanGuarantor> findByLoanIdAndGuarantorId(Long loanId, Long guarantorId);
}
