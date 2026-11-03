package uz.repay.card.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.Language;
import uz.repay.common.model.enums.Sex;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@ToString
@Entity
@Table(
        name = "guarantor",
        indexes = {
                @Index(name = "guarantor_person_code_ind", columnList = "person_code")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "guarantor_person_code_un", columnNames = {"person_code"})
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class Guarantor extends BaseEntity {
    @Id
    @SequenceGenerator(
            name = "guarantor_seq",
            sequenceName = "guarantor_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "guarantor_seq",
    strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "surname")
    private String surname;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "phone")
    private String phone;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "person_code")
    private String personCode;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Sex gender;

    @OneToMany(mappedBy = "guarantor")
    @ToString.Exclude
    private Set<LoanGuarantor> loanGuarantors = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "guarantor", fetch = FetchType.LAZY)
    private Set<GuarantorCard> guarantorCards = new HashSet<>();
}
