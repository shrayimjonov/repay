package uz.repay.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.repay.card.model.entity.GuarantorCard;
import uz.repay.card.model.entity.pk.GuarantorCardKey;

import java.util.Optional;


@Repository
public interface GuarantorCardRepository extends JpaRepository<GuarantorCard, GuarantorCardKey> {

    @Query(value = "select gc from GuarantorCard gc left join fetch gc.card left join fetch gc.guarantor where gc.id.guarantorId=:guarantorId and gc.id.cardId=:cardId")
    Optional<GuarantorCard> findByGuarantorIdAndCardId(Long guarantorId, Long cardId);
}
