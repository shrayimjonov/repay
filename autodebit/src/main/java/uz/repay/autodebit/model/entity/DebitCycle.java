package uz.repay.autodebit.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "debit_cycle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitCycle {
    @Id
    @Column(name = "cycle_id", length = 48)
    private String cycleId;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "deadline", nullable = false)
    private Instant deadline;

    @Column(name = "expected_count", nullable = false)
    private int expectedCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private DebitCycleStatus status;
}
