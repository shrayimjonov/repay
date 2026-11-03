package uz.repay.card.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "loan_temp")
@AllArgsConstructor
@NoArgsConstructor
public class TemporaryLoan {
    @SequenceGenerator(
            name = "temp_loan_seq",
            sequenceName = "temp_loan_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "temp_loan_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    private String loanExtId;

}
