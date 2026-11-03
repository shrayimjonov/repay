package uz.repay.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.repay.payment.model.entity.PCMaintenance;

@Repository
public interface PCMaintenanceRepository extends JpaRepository<PCMaintenance, Long> {

}
