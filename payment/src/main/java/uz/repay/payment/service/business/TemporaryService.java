package uz.repay.payment.service.business;

import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Processing;
import uz.repay.payment.model.entity.Temporary;
import uz.repay.payment.model.entity.Transaction;

import java.util.List;


public interface TemporaryService {
    void successSaveToTemporary(Transaction t,
                                CardType cardType,
                                Processing processing,
                                AddType addType);

    void checkManualTransactionFromABS();

    void checkManualTransactionFromABSV2();

    List<Temporary> findByLoanExtId(String loanExtId);
}
