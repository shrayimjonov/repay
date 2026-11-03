package uz.repay.card.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.card.model.enums.CardAction;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.utils.HttpServletUtils;

@Getter
@Setter
@ToString
@Entity
@Table(name = "deleted_cards",
indexes = {
        @Index(name = "del_card_card_id_ind", columnList = "card_id"),
        @Index(name = "del_card_loan_id_ind", columnList = "loan_id")
})
@AllArgsConstructor
@NoArgsConstructor
public class DeletedCards extends BaseEntity {
    @SequenceGenerator(name = "del_card_seq",
            sequenceName = "del_card_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "del_card_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    @Column(name = "card_id")
    private Long cardId;
    @Column(name = "loan_id")
    private Long loanId;
    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private CardAction action;
    @Column(name = "ip_address")
    private String ipAddress;

    public DeletedCards(Long cardId, Long loanId, CardAction cardAction) {
        this.cardId = cardId;
        this.loanId = loanId;
        this.action = cardAction;
        this.ipAddress = HttpServletUtils.getRequestIPAddress();
    }
}
