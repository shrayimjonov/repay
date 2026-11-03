package uz.repay.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.repay.card.model.entity.Status;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
}
