package uz.nodir.notification.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.nodir.notification.redis.entity.Fraud;

import java.util.List;
import java.util.UUID;

/**
 * @group: DavrCoders
 **/
@Repository
public interface FraudRepository extends CrudRepository<Fraud, UUID> {
    List<Fraud> findByPhone(String phone);

    List<Fraud> findByLoanExtId(String loanExtId);

    List<Fraud> findByLoanExtIdAndPhone(String loanExtId, String phone);
}
