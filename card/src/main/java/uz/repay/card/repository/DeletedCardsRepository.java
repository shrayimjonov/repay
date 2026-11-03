package uz.repay.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.repay.card.model.entity.DeletedCards;

@Repository
public interface DeletedCardsRepository extends JpaRepository<DeletedCards, Long> {
}
