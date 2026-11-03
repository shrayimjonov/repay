package uz.repay.payment.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.*;
import uz.repay.common.utils.BranchUtils;
import uz.repay.common.utils.HttpServletUtils;
import uz.repay.payment.utils.TranUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(
        name = "transaction",
        indexes = {
                @Index(name = "tran_loan_index", columnList = "loan_ext_id"),
                @Index(name = "tran_refnum_index", columnList = "ref_num"),
                @Index(name = "tran_create_date_ind", columnList = "created_date")
        })
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends BaseEntity {
    @SequenceGenerator(name = "tran_seq",
            sequenceName = "tran_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "tran_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    @Column(name = "branch")
    @Enumerated(EnumType.STRING)
    private Branch branch;

    @Column(name = "card_owner")
    private String cardOwner;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ResponseStatus status;

    @Column(name = "ref_num")
    private String refNum;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "reverse_reason")
    private String reverseReason;

    @OneToMany(mappedBy = "tranId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Bill> bills = new ArrayList<>();

    @Column(name = "loan_ext_id")
    private String loanExtId;

    @Column(name = "card_pan")
    private String cardPan;

    @Column(name = "pay_type")
    @Enumerated(EnumType.STRING)
    private AddType payType;

    @Column(name = "account")
    private String account;

    @Column(name = "general_id")
    private String generalId;

    @Column(name = "processing")
    @Enumerated(EnumType.STRING)
    private Processing processing;

    @Column(name = "terminal_id")
    private String terminalId;

    @Column(name = "rate")
    private Long rate;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;


    public Transaction(String loanExtId, String cardPan, String cardOwner, Long amount,
                       Processing processing, AddType type) {
        this.status = ResponseStatus.CREATED;
        this.loanExtId = loanExtId;
        this.branch = BranchUtils.getBranchByLoan(loanExtId);
        this.cardPan = cardPan;
        this.cardOwner = cardOwner;
        this.status = ResponseStatus.CREATED;
        this.amount = amount;
        this.processing = processing;
        this.payType = type;
        this.currency = TranUtils.takeCurrencyByPC(processing);
    }

    public Transaction(String loanExtId, Long amount,
                       AddType addType, String cardPan,
                       String cardOwner, String card,
                       Processing processing) {
        this.status = ResponseStatus.CREATED;
        this.loanExtId = loanExtId;
        this.branch = BranchUtils.getBranchByLoan(loanExtId);
        this.cardPan = cardPan;
        this.account = card;
        this.cardOwner = cardOwner;
        this.status = ResponseStatus.CREATED;
        this.amount = amount;
        this.processing = processing;
        this.payType = addType;
        this.currency = TranUtils.takeCurrencyByPC(processing);
    }
}
