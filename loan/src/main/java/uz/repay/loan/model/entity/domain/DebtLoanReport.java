package uz.repay.loan.model.entity.domain;

import java.time.LocalDate;

public interface DebtLoanReport {

    String getBranch();

    Integer getCount();

    Long getDebt_amount();

    LocalDate getCreated_date();
}
