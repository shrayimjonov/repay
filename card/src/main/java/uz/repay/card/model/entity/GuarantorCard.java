package uz.repay.card.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.card.model.entity.base.BaseCardEntity;
import uz.repay.card.model.entity.pk.GuarantorCardKey;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.utils.HttpServletUtils;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@Table(name = "guarantor_cards",
        indexes = {
                @Index(name = "guarantor_cards_guarantor_ind", columnList = "guarantor_id"),
                @Index(name = "guarantor_cards_card_ind", columnList = "card_id")
        })
@AllArgsConstructor
@NoArgsConstructor
public class GuarantorCard extends BaseCardEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234567L;

    @EmbeddedId
    private GuarantorCardKey id;

    @ManyToOne
    @MapsId("guarantorId")
    @JoinColumn(name = "guarantor_id")
    private Guarantor guarantor;


    public GuarantorCard(GuarantorCardKey guarantorCardKey, Guarantor guarantor, Card card, Payer payer, AddType addType) {
        this.id = guarantorCardKey;
        this.guarantor = guarantor;
        super.setCard(card);
        super.setPayer(payer);
        super.setAddType(addType);
        super.setIsDeleted(false);
        super.setIpAddress(HttpServletUtils.getRequestIPAddress());
        super.setIsConfirmed(true);
    }
}
