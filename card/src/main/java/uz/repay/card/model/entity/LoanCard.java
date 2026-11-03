package uz.repay.card.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.card.model.entity.base.BaseCardEntity;
import uz.repay.card.model.entity.pk.LoanCardKey;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Payer;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "loan_cards",
        indexes = {
                @Index(name = "loan_card_loan_ind", columnList = "loan_id"),
                @Index(name = "loan_card_card_ind", columnList = "card_id")
        })
@AllArgsConstructor
@NoArgsConstructor
public class LoanCard extends BaseCardEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234567L;

    @EmbeddedId
    private LoanCardKey id;

    @ManyToOne
    @MapsId("loanId")
    @JoinColumn(name = "loan_id")
    private Loan loan;


    public LoanCard(LoanCardKey loanCardKey, Loan loan,
                    Card card, Payer payer, boolean b,
                    String requestIPAddress, AddType addType) {
        this.id = loanCardKey;
        this.loan = loan;
        super.setCard(card);
        super.setPayer(payer);
        super.setAddType(addType);
        super.setIsDeleted(b);
        super.setIpAddress(requestIPAddress);
        super.setIsConfirmed(true);
    }

    public LoanCard(LoanCardKey loanCardKey, Loan loan,
                    Card card, Payer payer, boolean isDeleted,
                    String requestIPAddress, AddType addType,
                    boolean isConfirmed
                    ) {
        this.id = loanCardKey;
        this.loan = loan;
        super.setCard(card);
        super.setPayer(payer);
        super.setAddType(addType);
        super.setIsDeleted(isDeleted);
        super.setIpAddress(requestIPAddress);
        super.setIsConfirmed(isConfirmed);
    }
}
