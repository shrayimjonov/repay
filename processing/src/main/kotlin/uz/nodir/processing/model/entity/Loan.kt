package uz.nodir.processing.model.entity

import jakarta.persistence.*
import org.springframework.data.relational.core.mapping.Table
import uz.repay.common.model.entity.BaseEntity
import uz.repay.common.model.enums.Branch
import uz.repay.common.model.enums.Language


@Entity
@Table(name = "loan")
@jakarta.persistence.Table(name = "loan")
class Loan(
    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "branch")
    @Enumerated(EnumType.STRING)
    val branch: Branch? = null,

    @Column(name = "pinfl")
    val pinfl: String? = null,

    @Column(name = "tin")
    val tin: String? = null,

    @Column(name = "birthday")
    val birthday: String? = null,

    @Column(name = "external_id")
    @org.springframework.data.relational.core.mapping.Column("external_id")
    val extId: String? = null,

    @Column(name = "payer_name")
    val payerName: String? = null,

    @Column(name = "first_name")
    val firstName: String? = null,

    @Column(name = "patronymic")
    val patronymic: String? = null,

    @Column(name = "surname")
    val surname: String? = null,

    @Column(name = "is_deleted")
    val isDeleted: Boolean = false,

    @Column(name = "passport_serial")
    val passportSerial: String? = null,

    @Column(name = "passport_number")
    val passportNumber: String? = null,

    @Column(name = "payer_phone")
    val payerPhone: String? = null,

    @Column(name = "id_nibbd")
    val idNibbd: String? = null,

    @Column(name = "code_gender")
    val codeGender: String? = null,

    @Column(name = "birth_place")
    val birthPlace: String? = null,

    @Column(name = "passport_date_expiration")
    val passportDateExpiration: String? = null,

    @Column(name = "passport_date_registration")
    val passportDateRegistration: String? = null,

    @Column(name = "is_convicted")
    val isConvicted: Boolean = false,

    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER)
    val loanCards: Set<LoanCard> = HashSet(),

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    val language: Language? = null,

    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER)
    val guarantors: Set<LoanGuarantor>? = HashSet()
) : BaseEntity()
