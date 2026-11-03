package uz.repay.card.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.repay.card.redis.entity.Switcher;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SwitcherRepository extends CrudRepository<Switcher, UUID> {
    Optional<Switcher> findByName(String name);
}
