package uz.repay.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.repay.card.model.entity.AutoPay;

import java.util.Optional;

@Repository
public interface AutoPayRepository extends JpaRepository<AutoPay, Long> {

    Optional<AutoPay> findByLoanExtId(String loanExtId);
}
