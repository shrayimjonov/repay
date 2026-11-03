package uz.repay.autodebit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.autodebit.model.entity.DebitAttempt;

import java.time.Instant;

public interface DebitAttemptRepository extends JpaRepository<DebitAttempt, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO debit_attempt (loan_ext_id, cycle_id, branch_id, debit_date, status, created_at)
            VALUES (:loanExtId, :cycleId, :branchId, CURRENT_DATE, 'PROCESSING', now())
            ON CONFLICT DO NOTHING
            """, nativeQuery = true)
    int claim(@Param("loanExtId") String loanExtId,
              @Param("cycleId") String cycleId,
              @Param("branchId") String branchId);

    @Query(value = """
            SELECT EXISTS(
                SELECT 1 FROM debit_attempt
                WHERE loan_ext_id = :loanExtId
                  AND status IN ('FAILED_UNKNOWN', 'EXPIRED_UNKNOWN')
                  AND created_at > :since
            )
            """, nativeQuery = true)
    boolean hasUnknownOutcomeSince(@Param("loanExtId") String loanExtId,
                                   @Param("since") Instant since);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE debit_attempt
            SET status = :status, failure_reason = LEFT(:reason, 256), updated_at = now()
            WHERE loan_ext_id = :loanExtId AND cycle_id = :cycleId
            """, nativeQuery = true)
    int markStatus(@Param("loanExtId") String loanExtId,
                   @Param("cycleId") String cycleId,
                   @Param("status") String status,
                   @Param("reason") String reason);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE debit_attempt
            SET status = 'EXPIRED_UNKNOWN',
                failure_reason = 'Stale PROCESSING claim expired by janitor',
                updated_at = now()
            WHERE status = 'PROCESSING' AND created_at < :cutoff
            """, nativeQuery = true)
    int expireStaleProcessing(@Param("cutoff") Instant cutoff);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM debit_attempt
            WHERE id IN (
                SELECT id FROM debit_attempt
                WHERE created_at < :cutoff
                LIMIT :batchSize
            )
            """, nativeQuery = true)
    int purgeBatchOlderThan(@Param("cutoff") Instant cutoff,
                            @Param("batchSize") int batchSize);
}