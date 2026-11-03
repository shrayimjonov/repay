package uz.repay.payment.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.Processing;

import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "processing_maintenance",
        uniqueConstraints = {
                @UniqueConstraint(name = "onePcMaintenance", columnNames = "processing")
        })
@AllArgsConstructor
@NoArgsConstructor
public class PCMaintenance extends BaseEntity {
    @SequenceGenerator(name = "pc_main_seq",
            sequenceName = "pc_main_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "pc_main_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private Processing processing;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Fetch(FetchMode.JOIN)
    private List<ABSSetting> absSettings;

    public PCMaintenance(Processing processing) {
        this.processing = processing;
    }
}
