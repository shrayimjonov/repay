package nodir.paymo_add.service;

import lombok.RequiredArgsConstructor;
import nodir.paymo_add.entity.Loan;
import nodir.paymo_add.repository.LoanRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
    private final LoanRepository repository;

    @Override
    public Loan findLoanInfo(String loanId) {
        return repository.findLoanInfo(loanId);
    }
}
