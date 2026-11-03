package uz.repay.card.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.StatusType;

@Getter
@Setter
@ToString
@Entity
@Table(name = "status",
        indexes = {
                @Index(name = "stat_proces_code_ind", columnList = "code, processing"),
                @Index(name = "stat_proces_type_ind", columnList = "type, processing")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "stat_oneCode_OneProc", columnNames = {"code", "processing", "type"})
        })
@AllArgsConstructor
@NoArgsConstructor
public class Status extends BaseEntity {
    @SequenceGenerator(name = "card_stat_seq",
            sequenceName = "card_stat_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "card_stat_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    @Column(name = "code", nullable = false)
    private String code;
    private String definition;
    private String definitionUz;
    @Enumerated(EnumType.STRING)
    private Processing processing;
    @Enumerated(EnumType.STRING)
    private StatusType type;
}
