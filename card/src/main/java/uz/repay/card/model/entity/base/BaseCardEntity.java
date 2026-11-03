package uz.repay.card.model.entity.base;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.card.model.entity.Card;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Payer;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseCardEntity extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234567L;

    @ManyToOne
    @MapsId("cardId")
    @JoinColumn(name = "card_id")
    private Card card;

    @Enumerated(EnumType.STRING)
    private Payer payer;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "added_type")
    @Enumerated(EnumType.STRING)
    private AddType addType;

    @Column(name = "is_confirmed")
    private Boolean isConfirmed;
}
