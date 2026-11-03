package uz.repay.card.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.ResponseStatus;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(
        name = "reconciliation",
        indexes = {
                @Index(name = "reco_terminal_ind", columnList = "terminal_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class Reconciliation extends BaseEntity {
    @SequenceGenerator(
            name = "reco_seq",
            sequenceName = "reco_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "reco_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    @Column(name = "ref_num")
    private String refNum;
    @Column(name = "terminal_id")
    private String terminalId;
    @Column(name = "payment_id")
    private String paymentId;
    @Column(name = "credits_reversal_number")
    private Integer creditsReversalNumber;
    @Column(name = "credits_reversal_amount")
    private Long creditsReversalAmount;
    @Column(name = "debits_number")
    private Integer debitsNumber;
    @Column(name = "debits_amount")
    private Long debitsAmount;
    @Column(name = "response")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<Object, Object> response;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ResponseStatus status;
    @Column(name = "session")
    private UUID sessionId;

    public Reconciliation(String terminalId,
                          ResponseStatus status,
                          UUID sessionId) {
        this.terminalId = terminalId;
        this.status = status;
        this.sessionId = sessionId;
    }


}
