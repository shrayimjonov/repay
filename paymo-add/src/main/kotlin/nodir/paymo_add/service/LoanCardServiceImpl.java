package nodir.paymo_add.service;

import lombok.RequiredArgsConstructor;
import lombok.var;
import nodir.paymo_add.entity.Loan;
import nodir.paymo_add.entity.LoanCard;
import nodir.paymo_add.exception.CardNotFound;
import nodir.paymo_add.exception.EntityNotFound;
import nodir.paymo_add.exception.TransactionFinished;
import nodir.paymo_add.repository.LoanCardRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanCardServiceImpl implements LoanCardService {
    private final LoanCardRepository repository;


    @Override
    public List<LoanCard> findLoanCardByLoanId(String loanId) {
        return repository.findByExternal_id(loanId);
    }

    @Override
    public List<LoanCard> findLoanCardByCard(String card) {
        return repository.findByCard_pan(card);
    }

    @Override
    public List<LoanCard> findLoanCardByPhone(String phone) {
        return repository.findByCard_phone(phone);
    }

    @Override
    public LoanCard findByLoanCardId(Long id) {
        var loanCard = repository.findByLoanCardId(id);
        if (loanCard.isPresent()) {
            return loanCard.get();
        } else {
            throw new EntityNotFound("LoanCard entity with id #" + id + " not found in database");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUzCardContract(Long id) {
        var loanCard = findByLoanCardId(id);
        if (loanCard.getUzcard_client_id() == null) {
            repository.updateUzCardContract(id);
        } else {
            throw new CardNotFound(id.toString(), "GENESIS card");
        }
    }
}
