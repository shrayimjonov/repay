package uz.repay.card.service.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import uz.repay.card.model.entity.Reconciliation;

public interface ReconciliationDAO {
    Reconciliation save(Reconciliation reconciliation);

    Page<Reconciliation> findAll(Specification<Reconciliation> specification, Pageable pageable);
}
