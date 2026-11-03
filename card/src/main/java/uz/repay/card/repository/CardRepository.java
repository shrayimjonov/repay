package uz.repay.card.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.repay.card.model.entity.Card;
import uz.repay.card.repository.projection.HumoAutoDebitCardProjection;
import uz.repay.card.repository.projection.HumoLoanCardProjection;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    @Query(value = "select c from Card c where c.pan=:pan")
    Optional<Card> findCardByCard(String pan);

    @Query(value = "select c from Card c WHERE c.isActive=:isActive")
    List<Card> getCardsByStatus(Boolean isActive);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select c from Card c where c.token=:token")
    Optional<Card> findCardByToken(String token);

    List<Card> findAllByProcessing(Processing processing);

    @Query("""
            select l.extId as loanExtId, c.id as cardId, c.pan as pan
            from LoanCard lc
            join lc.loan l
            join lc.card c
            where l.extId in (:loanExtIds)
              and l.isDeleted = false
              and c.processing = :processing
              and c.isActive = true
              and c.isConfirmed = true
              and lc.isDeleted = false
              and lc.isConfirmed = true
              and lc.payer in (:payers)
            """)
    List<HumoLoanCardProjection> findHumoLoanCardsByLoanExtIds(
            @Param("loanExtIds") List<String> loanExtIds,
            @Param("processing") Processing processing,
            @Param("payers") List<Payer> payers
    );

    @Query("""
            select distinct c.id as cardId, c.pan as pan, c.maskedPan as maskedPan
            from LoanCard lc
            join lc.loan l
            join lc.card c
            where l.isDeleted = false
              and l.isAutoDebit = true
              and c.processing = :processing
              and c.isActive = true
              and c.isConfirmed = true
              and lc.isDeleted = false
              and lc.isConfirmed = true
              and lc.payer in (:payers)
              and mod(c.id, :partitionCount) in (:partitions)
            """)
    List<HumoAutoDebitCardProjection> findActiveAutoDebitHumoCardsByKafkaPartitions(
            @Param("processing") Processing processing,
            @Param("payers") List<Payer> payers,
            @Param("partitions") Collection<Integer> partitions,
            @Param("partitionCount") int partitionCount
    );
}

