package uz.repay.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.repay.card.model.entity.TemporaryLoan;

@Repository
public interface TemporaryLoanRepository extends JpaRepository<TemporaryLoan, Long> {
}
