package uz.repay.loan.model.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LoanFeaturePK implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234567L;

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "id")
    private UUID id;

}
