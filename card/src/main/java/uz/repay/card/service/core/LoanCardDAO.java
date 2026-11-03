package uz.repay.card.service.core;

import uz.repay.card.model.entity.Card;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.model.entity.LoanCard;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Payer;

import java.util.List;
import java.util.Optional;

public interface LoanCardDAO {

    Optional<LoanCard> findByLoanAndCard(Long loanId, Long cardId);

    Loan create(Loan loan, Card card, Payer payer, AddType addType);

    @Deprecated
    void deleteOrApprove(Loan loan, Long cardId, Boolean isDelete);

    List<LoanCard> findByCardToken(String token);

    LoanCard save(LoanCard loanCard);

    LoanCard createV2(Loan loan, Card card, Payer payer, AddType addType, Boolean isConfirmed);

    List<LoanCard> findByCardPan(String pan);
}
