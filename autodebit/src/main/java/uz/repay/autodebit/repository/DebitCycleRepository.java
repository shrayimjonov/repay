package uz.repay.autodebit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.autodebit.model.entity.DebitCycle;

import java.time.Instant;

public interface DebitCycleRepository extends JpaRepository<DebitCycle, String> {

    @Query(value = """
            SELECT EXISTS(
                SELECT 1 FROM debit_cycle
                WHERE status = 'RUNNING' AND deadline > now()
            )
            """, nativeQuery = true)
    boolean hasActiveCycle();

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE debit_cycle SET status = 'EXPIRED'
            WHERE status = 'RUNNING' AND deadline <= now()
            """, nativeQuery = true)
    int expireOverdueCycles();

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE debit_cycle SET expected_count = :expectedCount WHERE cycle_id = :cycleId
            """, nativeQuery = true)
    int updateExpectedCount(@Param("cycleId") String cycleId,
                            @Param("expectedCount") int expectedCount);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM debit_cycle WHERE started_at < :cutoff
            """, nativeQuery = true)
    int deleteOlderThan(@Param("cutoff") Instant cutoff);
}