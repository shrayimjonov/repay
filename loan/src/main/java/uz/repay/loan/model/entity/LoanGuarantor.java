package uz.repay.loan.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.utils.HttpServletUtils;
import uz.repay.loan.model.entity.pk.LoanGuarantorKey;

@Getter
@Setter
@ToString
@Entity
@Table(
        name = "loan_guarantors",
        indexes = {
                @Index(name = "loan_guarantor_ind", columnList = "loan_id,guarantor_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class LoanGuarantor extends BaseEntity {
    @EmbeddedId
    private LoanGuarantorKey id;

    @ManyToOne
    @MapsId("loanId")
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @ManyToOne
    @MapsId("guarantorId")
    @JoinColumn(name = "guarantor_id")
    private Guarantor guarantor;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "ip_address")
    private String ipAddress;


    public LoanGuarantor(LoanGuarantorKey pk, Loan loan, Guarantor guarantor) {
        this.id = pk;
        this.loan = loan;
        this.guarantor = guarantor;
        this.isDeleted = false;
        this.ipAddress = HttpServletUtils.getRequestIPAddress();
    }
}
