package uz.nodir.chat_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.nodir.chat_service.dto.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
