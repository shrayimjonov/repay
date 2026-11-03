package uz.repay.loan.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.entity.BaseEntity;

@Getter
@Setter
@ToString
@Entity
@Table(name = "debt_loan",
        indexes = {
                @Index(name = "loanExtId", columnList = "loan_ext_id", unique = true)
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "loanExtId", columnNames = "loan_ext_id")
        })
@AllArgsConstructor
@NoArgsConstructor
public class DebtLoan extends BaseEntity {
    @SequenceGenerator(name = "debt_loan_seq",
            sequenceName = "debt_loan_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "debt_loan_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    @Column(name = "payer_name")
    private String payerName;

    @Column(name = "payer_phone")
    private String payerPhone;

    @Column(name = "loan_ext_id")
    private String loanExtId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "recommended")
    private Long recommended;

    @Column(name = "total")
    private Long total;

    @Column(name = "passport_serial")
    private String passportSerial;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "pinfl")
    private String pinfl;

    @Column(name = "is_insolvent")
    private Boolean isInsolvent;

    @Column(name = "loan_id")
    private Long loanId;

    public DebtLoan(Loan loan, Long recommended, Long total) {
        this.payerName = loan.getPayerName();
        this.payerPhone = loan.getPayerPhone();
        this.loanExtId = loan.getExtId();
        this.branch = loan.getBranch().getCode();
        this.recommended = recommended;
        this.total = total;
        this.pinfl = loan.getPinfl();
        this.passportNumber = loan.getPassportNumber();
        this.passportSerial = loan.getPassportSerial();
        this.isInsolvent = loan.getIsInsolvent();
        this.loanId = loan.getId();
    }

}
