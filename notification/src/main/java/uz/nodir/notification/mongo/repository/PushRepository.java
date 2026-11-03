package uz.nodir.notification.mongo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.nodir.notification.mongo.entity.Push;

import java.util.List;
import java.util.UUID;

@Repository
public interface PushRepository extends CrudRepository<Push, UUID> {

    List<Push> findAllByLoanExtId(String loanExtId);

    List<Push> findAllByUsername(String username);
}
