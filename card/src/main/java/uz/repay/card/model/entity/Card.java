package uz.repay.card.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ServiceType;
import uz.repay.common.utils.CardUtils;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "card",
        indexes = {
                @Index(name = "card_ind", columnList = "pan")
        })
@AllArgsConstructor
@NoArgsConstructor
public class Card extends BaseEntity {
    @SequenceGenerator(name = "card_seq",
            sequenceName = "card_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "card_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    private String pan;
    private String expiry;
    private String maskedPan;
    private String holder;
    @Enumerated(EnumType.STRING)
    private Processing processing;
    private String phone;
    private Boolean isConfirmed;
    private String token;
    @Enumerated(EnumType.STRING)
    private ServiceType service;
    @OneToMany(mappedBy = "card")
    @ToString.Exclude
    private Set<LoanCard> loanCards = new HashSet<>();
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Deprecated
    public Card(String pan, String expiry, Processing processing) {
        this.pan = CardUtils.encryptPan(pan);
        this.expiry = expiry;
        this.maskedPan = CardUtils.maskPanDigits(pan);
        this.processing = processing;
        this.isConfirmed = false;
        this.service = ServiceType.REPAY;
        this.isActive = true;
        this.currency = CardUtils.isSumPC(processing) ? Currency.UZS : Currency.USD;
    }

    public Card(String pan, String expiry, String holder, Processing pc, String phone, String token, Boolean isConfirmed,
                Currency currency) {
        this.pan = CardUtils.encryptPan(pan);
        this.expiry = expiry;
        this.maskedPan = CardUtils.maskPanDigits(pan);
        this.holder = holder;
        this.processing = pc;
        this.phone = phone;
        this.token = token;
        this.isConfirmed = isConfirmed;
        this.service = ServiceType.REPAY;
        this.isActive = true;
        this.currency = currency;
    }

    public Card(String pan, String expiry, Processing processing, String token) {
        this.pan = CardUtils.encryptPan(pan);
        this.expiry = expiry;
        this.maskedPan = CardUtils.maskPanDigits(pan);
        this.processing = processing;
        this.isConfirmed = false;
        this.service = ServiceType.REPAY;
        this.isActive = true;
        this.currency = CardUtils.isSumPC(processing) ? Currency.UZS : Currency.USD;
        this.token = token;
    }
}
