package uz.repay.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.repay.payment.model.entity.ABSSetting;

@Repository
public interface ABSSettingRepository extends JpaRepository<ABSSetting, Long> {
}
