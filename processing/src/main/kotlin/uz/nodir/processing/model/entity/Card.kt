package uz.nodir.processing.model.entity

import jakarta.persistence.*
import uz.repay.common.model.entity.BaseEntity
import uz.repay.common.model.enums.Currency
import uz.repay.common.model.enums.Processing


@Entity
@Table(name = "card")
class Card(
    @SequenceGenerator(name = "card_seq", sequenceName = "card_seq", allocationSize = 1)
    @GeneratedValue(generator = "card_seq", strategy = GenerationType.SEQUENCE)
    @Id
    @org.springframework.data.annotation.Id
    val id: Long? = null,


    @Column(name = "pan")
    val pan: String? = null,

    @Column(name = "expiry")
    val expiry: String? = null,

    @Column(name = "masked_pan")
    val maskedPan: String? = null,

    @Column(name = "holder")
    val holder: String? = null,

    @Column(name = "processing")
    val processing: Processing? = null,

    @Column(name = "phone")
    val phone: String? = null,

    @Column(name = "is_confirmed")
    val isConfirmed: Boolean? = null,

    @Column(name = "is_active")
    val isActive: Boolean? = null,

    @Column(name = "token")
    val token: String? = null,

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    val currency: Currency? = null,


    ): BaseEntity()