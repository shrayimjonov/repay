package nodir.paymo_add.service;

import nodir.paymo_add.entity.Loan;

public interface LoanService {
    Loan findLoanInfo(String loanId);
}
