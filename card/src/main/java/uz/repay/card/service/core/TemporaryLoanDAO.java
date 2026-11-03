package uz.repay.card.service.core;

import uz.repay.card.model.entity.TemporaryLoan;

import java.util.List;

public interface TemporaryLoanDAO {

    List<TemporaryLoan> findAll();
}
