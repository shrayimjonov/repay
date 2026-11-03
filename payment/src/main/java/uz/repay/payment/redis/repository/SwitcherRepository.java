package uz.repay.payment.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.repay.payment.redis.entity.Switcher;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SwitcherRepository extends CrudRepository<Switcher, UUID> {
    Optional<Switcher> findByName(String name);
}
