package nodir.paymo_add.service;

import nodir.paymo_add.entity.LoanCard;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LoanCardService {
    List<LoanCard> findLoanCardByLoanId(String loanId);
    List<LoanCard> findLoanCardByCard(String card);
    List<LoanCard> findLoanCardByPhone(String phone);
    LoanCard findByLoanCardId(Long id);
    void updateUzCardContract(Long id);
}
