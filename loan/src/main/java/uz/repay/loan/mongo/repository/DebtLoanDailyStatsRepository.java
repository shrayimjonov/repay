package uz.repay.loan.mongo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.repay.loan.mongo.entity.DebtLoanDailyStats;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface DebtLoanDailyStatsRepository extends CrudRepository<DebtLoanDailyStats, UUID> {

    List<DebtLoanDailyStats> findByCreatedDate(LocalDate createdDate);

    Optional<DebtLoanDailyStats> findByCreatedDateAndBranch(LocalDate createdDate, String branch);
}
