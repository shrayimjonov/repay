package uz.repay.card.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Language;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "loan")
public class Loan extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch")
    @Enumerated(EnumType.STRING)
    private Branch branch;

    @Column(name = "pinfl")
    private String pinfl;

    @Column(name = "tin")
    private String tin;

    @Column(name = "birthday")
    private String birthday;

    @Column(name = "external_id")
    private String extId;

    @Column(name = "payer_name")
    private String payerName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "surname")
    private String surname;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "passport_serial")
    private String passportSerial;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "payer_phone")
    private String payerPhone;

    @Column(name = "id_nibbd")
    private String idNibbd;

    @Column(name = "code_gender")
    private String codeGender;

    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "passport_date_expiration")
    private String passportDateExpiration;

    @Column(name = "passport_date_registration")
    private String passportDateRegistration;

    @Column(name = "is_convicted")
    private Boolean isConvicted = false;

    @ToString.Exclude
    @OneToMany(mappedBy = "loan", fetch = FetchType.LAZY)
    private Set<LoanCard> loanCards = new HashSet<>();

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

    @ToString.Exclude
    @OneToMany(mappedBy = "loan", fetch = FetchType.LAZY)
    private Set<LoanGuarantor> guarantors = new HashSet<>();

    @Transient
    private ArrayList<String> pans = new ArrayList<>();

    @Column(name = "is_auto_debit")
    private Boolean isAutoDebit;



}
