package uz.repay.payment.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.repay.payment.redis.entity.Fraud;

import java.util.List;
import java.util.UUID;

@Repository
public interface FraudRepository extends CrudRepository<Fraud, UUID> {
    List<Fraud> findByLoanId(String loanId);

    List<Fraud> findByLoanIdAndPan(String loanId, String pan);

    List<Fraud> findAllByLoanIdIn(List<String> loanIds);

    void deleteAllByLoanIdIn(List<String> loanIds);
}
