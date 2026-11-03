package uz.repay.card.service.core;

import uz.repay.card.model.entity.AutoPay;

import java.util.Optional;

public interface AutoPayDAO {
    AutoPay save(AutoPay autoPay);

    Optional<AutoPay> findByLoanExtId(String loanExtId);
}
