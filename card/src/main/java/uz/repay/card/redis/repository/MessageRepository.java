package uz.repay.card.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.repay.card.redis.entity.Message;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends CrudRepository<Message, UUID> {

    Optional<Message> findByCardAndPhone(String card, String phone);
    Optional<Message> findById(String id);
}
