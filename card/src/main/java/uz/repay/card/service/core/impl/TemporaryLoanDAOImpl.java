package uz.repay.card.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.entity.TemporaryLoan;
import uz.repay.card.repository.TemporaryLoanRepository;
import uz.repay.card.service.core.TemporaryLoanDAO;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemporaryLoanDAOImpl implements TemporaryLoanDAO {
    private final TemporaryLoanRepository repository;

    @Override
    public List<TemporaryLoan> findAll() {
        log.info("Get all temporary loans...");
        return repository.findAll();
    }
}
