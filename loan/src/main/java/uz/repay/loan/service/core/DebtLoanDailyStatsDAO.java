package uz.repay.loan.service.core;

import uz.repay.loan.mongo.entity.DebtLoanDailyStats;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DebtLoanDailyStatsDAO {
    void save(DebtLoanDailyStats debtLoanDailyStats);

    List<DebtLoanDailyStats> findStatsByDate(LocalDate date);

    Optional<DebtLoanDailyStats> findStatsByDateAndBranch(LocalDate date, String branch);

    Optional<DebtLoanDailyStats> findStatsById(UUID id);
}
