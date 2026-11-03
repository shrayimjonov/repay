package uz.repay.card.model.entity.pk;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class GuarantorCardKey implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567L;

    @Column(name = "guarantor_id")
    private Long guarantorId;


    @Column(name = "card_id")
    private Long cardId;

}
