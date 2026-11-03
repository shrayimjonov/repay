package uz.repay.loan.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.entity.BaseEntity;

@Getter
@Setter
@ToString
@Entity
@Table(name = "note",
        indexes = {
                @Index(name = "note_loan_ext_id_ind", columnList = "loan_ext_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class Note extends BaseEntity {
    @SequenceGenerator(
            name = "note_seq",
            sequenceName = "note_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "note_seq",
            strategy = GenerationType.SEQUENCE
    )
    @Id
    private Long id;

    @Column(name = "loan_ext_id")
    private String loanExtId;

    @Column(name = "note", columnDefinition = "TEXT")
    private String text;

    public Note(String loanExtId, String note) {
        this.loanExtId = loanExtId;
        this.text = note;
    }
}
