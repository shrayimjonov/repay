package uz.repay.payment.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Processing;

@Getter
@Setter
@ToString
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Temporary {
    @SequenceGenerator(name = "temp_seq",
            sequenceName = "temp_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "temp_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    @Column(name = "loan_ext_id")
    private String loanExtId;
    @Column(name = "amount")
    private Long amount;
    @Column(name = "card_type")
    @Enumerated(EnumType.STRING)
    private CardType cardType;
    @Column(name = "card")
    private String card;
    @Column(name = "processing")
    @Enumerated(EnumType.STRING)
    private Processing processing;
    @Column(name = "card_pan")
    private String cardPan;
    @Column(name = "owner")
    private String owner;
    @Column(name = "add_type")
    @Enumerated(EnumType.STRING)
    private AddType addType;
    @Column(name = "payment_id")
    private String paymentId;
    @Column(name = "tran_id")
    private Long tranId;


    public Temporary(String loanExtId,
                     Long amount,
                     CardType cardType,
                     String card,
                     Processing processing,
                     String cardPan,
                     String owner,
                     String paymentId,
                     Long tranId,
                     AddType addType) {
        this.loanExtId = loanExtId;
        this.amount = amount;
        this.cardType = cardType;
        this.card = card;
        this.processing = processing;
        this.cardPan = cardPan;
        this.owner = owner;
        this.paymentId = paymentId;
        this.tranId = tranId;
        this.addType = addType;
    }
}
