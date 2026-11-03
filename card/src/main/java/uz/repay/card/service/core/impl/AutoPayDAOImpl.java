package uz.repay.card.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.card.model.entity.AutoPay;
import uz.repay.card.repository.AutoPayRepository;
import uz.repay.card.service.core.AutoPayDAO;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutoPayDAOImpl implements AutoPayDAO {
    private final AutoPayRepository repository;

    @Override
    @Transactional
    public AutoPay save(AutoPay autoPay) {
        log.info("Save/Update autoPay entity: {}", autoPay);
        return repository.save(autoPay);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AutoPay> findByLoanExtId(String loanExtId){
        log.info("Find autoPay by loan ext id: {}", loanExtId);
        return repository.findByLoanExtId(loanExtId);
    }


}
