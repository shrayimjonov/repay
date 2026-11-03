package uz.repay.card.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.card.model.entity.Reconciliation;
import uz.repay.card.repository.ReconciliationRepository;
import uz.repay.card.service.core.ReconciliationDAO;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReconciliationDAOImpl implements ReconciliationDAO {
    private final ReconciliationRepository repository;

    @Override
    public Reconciliation save(Reconciliation reconciliation) {
        log.info("Save reconciliation: {}", reconciliation);
        return repository.save(reconciliation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Reconciliation> findAll(Specification<Reconciliation> specification, Pageable pageable) {
        log.info("Find all reconciliation with specifications");
        return repository.findAll(specification, pageable);
    }
}
