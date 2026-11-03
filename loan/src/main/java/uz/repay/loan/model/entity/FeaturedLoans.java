package uz.repay.loan.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.loan.model.entity.embedded.LoanFeaturePK;

@Getter
@Setter
@ToString
@Entity
@Table(name = "featured_loans",
        indexes = {
                @Index(name = "feat_loan_user_ind", columnList = "created_by")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "feat_oneUserOneLoanExt_un", columnNames = {"loan_id", "created_by"})
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class FeaturedLoans extends BaseEntity {
    @EmbeddedId
    LoanFeaturePK id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("loanId")
    @JoinColumn(name = "loan_id")
    Loan loan;



}
