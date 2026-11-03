package uz.repay.card.mongo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.repay.card.mongo.entity.CardData;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardDataRepository extends CrudRepository<CardData, UUID> {

    List<CardData> findByCardId(Long id);
}
