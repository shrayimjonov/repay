package uz.nodir.scoring.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.Processing;


@Getter
@Setter
@ToString
@Entity
@Table(name = "limit_type",
        indexes = {
                @Index(name = "scoring_limit_type_idx", columnList = "type")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "scoring_limit_type_pc_un", columnNames = {"type", "description"})
        })
@AllArgsConstructor
@NoArgsConstructor
public class Limit extends BaseEntity {

    @SequenceGenerator(
            name = "limit_type_seq",
            sequenceName = "limit_type_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "limit_type_seq",
            strategy = GenerationType.SEQUENCE
    )
    @Id
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "pc")
    @Enumerated(EnumType.STRING)
    private Processing processing;

    public Limit(String type, String description, Processing processing) {
        this.type = type;
        this.description = description;
        this.processing = processing;
    }
}
