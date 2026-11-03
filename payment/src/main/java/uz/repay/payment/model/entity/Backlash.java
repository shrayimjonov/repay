package uz.repay.payment.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.StatusType;
import uz.repay.common.utils.DateUtils;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "backlash",
        indexes = {
                @Index(name = "backlash_used_ind", columnList = "is_used")
        })
@AllArgsConstructor
@NoArgsConstructor
public class Backlash {
    @SequenceGenerator(name = "resp_seq",
            sequenceName = "resp_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "resp_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime = DateUtils.getDateTimeNow();
    private Long spentTime;
    @Column(name = "loan_ext_id", nullable = false)
    private String loanExtId;
    @Enumerated(EnumType.STRING)
    private Processing processing;
    @Column(name = "updated_time")
    private LocalDateTime updatedTime = DateUtils.getDateTimeNow();
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private StatusType type;


    public Backlash(String loanExtId, Processing processing, Long spentTime, StatusType type) {
        this.isUsed = false;
        this.loanExtId = loanExtId;
        this.spentTime = spentTime;
        this.processing = processing;
        this.type = type;
    }
}
