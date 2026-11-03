package uz.repay.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.repay.card.model.entity.Reconciliation;

@Repository
public interface ReconciliationRepository extends JpaRepository<Reconciliation, Long>, JpaSpecificationExecutor<Reconciliation> {

}
