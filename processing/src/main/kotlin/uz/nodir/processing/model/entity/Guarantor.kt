package uz.nodir.processing.model.entity

import jakarta.persistence.*
import lombok.ToString
import uz.repay.common.model.entity.BaseEntity
import uz.repay.common.model.enums.Language
import uz.repay.common.model.enums.Sex
import java.time.LocalDate


@Entity
@Table(name = "guarantor")
class Guarantor(
    @Id
    @org.springframework.data.annotation.Id
    @SequenceGenerator(
        name = "guarantor_seq",
        sequenceName = "guarantor_seq",
        allocationSize = 1)
    @GeneratedValue(
        generator = "guarantor_seq",
        strategy = GenerationType.SEQUENCE
    )
    val id: Long? = null,

    @Column(name = "first_name")
    val firstName: String? = null,

    @Column(name = "surname")
    val surname: String? = null,

    @Column(name = "patronymic")
    val patronymic: String? = null,

    @Column(name = "birthday")
    val birthday: LocalDate? = null,

    @Column(name = "phone")
    val phone: String? = null,

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    val language: Language? = null,

    @Column(name = "person_code")
    val personCode: String? = null,

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    val gender: Sex? = null,

    @OneToMany(mappedBy = "guarantor")
    @ToString.Exclude
    val loanGuarantors: Set<LoanGuarantor> = HashSet(),

    @ToString.Exclude
    @OneToMany(
        mappedBy = "guarantor",
        fetch = FetchType.EAGER
    )
    val guarantorCards: Set<GuarantorCard> = HashSet()
) : BaseEntity()