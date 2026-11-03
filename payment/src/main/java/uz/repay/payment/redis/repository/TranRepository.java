package uz.repay.payment.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.repay.payment.redis.entity.Tran;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TranRepository extends CrudRepository<Tran, UUID> {

    Optional<Tran> findByTransactionId(Long tranId);
}
