package uz.repay.loan.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.loan.mongo.entity.DebtLoanDailyStats;
import uz.repay.loan.mongo.repository.DebtLoanDailyStatsRepository;
import uz.repay.loan.service.core.DebtLoanDailyStatsDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class DebtLoanDailyStatsDAOImpl implements DebtLoanDailyStatsDAO {
    private final DebtLoanDailyStatsRepository repository;

    @Override
    @Transactional
    public void save(DebtLoanDailyStats debtLoanDailyStats) {
        log.info("Save debt loan daily stats data: {}", debtLoanDailyStats);
        repository.save(debtLoanDailyStats);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DebtLoanDailyStats> findStatsByDate(LocalDate date) {
        log.info("Find debt loan daily stats by date: {}", date);
        return repository.findByCreatedDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DebtLoanDailyStats> findStatsByDateAndBranch(LocalDate date, String branch) {
        log.info("Find debt loan daily stats by date: {} for branch: {}", date, branch);
        return repository.findByCreatedDateAndBranch(date, branch);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DebtLoanDailyStats> findStatsById(UUID id) {
        log.info("Find debt loan daily stats by id: {}", id);
        return repository.findById(id);
    }

}
