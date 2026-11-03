package uz.repay.card.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.card.model.dto.svgate.response.SVGateAutoPayCardsResult;
import uz.repay.common.model.entity.BaseEntity;

@Getter
@Setter
@ToString
@Entity
@Table(name = "autopay_cards",
        indexes = {
                @Index(name = "autopay_cards_token_ind", columnList = "token")
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class AutoPayCard extends BaseEntity {

    @SequenceGenerator(name = "autopay_seq",
            sequenceName = "autopay_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "autopay_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    @Column(name = "holder")
    private String holder;
    @Column(name = "token")
    private String token;
    @Column(name = "pan")
    private String pan;
    @ManyToOne
    @JoinColumn(name = "autopay_id")
    private AutoPay autoPay;

    public AutoPayCard(SVGateAutoPayCardsResult c) {
        this.holder = c.getFullName();
        this.token = c.getToken();
        this.pan = c.getPan();
    }

    public AutoPayCard(SVGateAutoPayCardsResult c, AutoPay autoPay) {
        this.holder = c.getFullName();
        this.token = c.getToken();
        this.pan = c.getPan();
        this.autoPay = autoPay;
    }
}
