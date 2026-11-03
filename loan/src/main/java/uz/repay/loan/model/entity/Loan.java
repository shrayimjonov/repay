package uz.repay.loan.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.Language;
import uz.repay.common.model.enums.Subject;
import uz.repay.common.utils.BranchUtils;
import uz.repay.loan.model.dto.nciBilling.response.ClientInfoResponse;
import uz.repay.loan.model.dto.nciGateway.response.NCILoanInfoRepayResponse;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "loan",
        indexes = {
                @Index(name = "external_id", columnList = "external_id", unique = true)
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "external_id", columnNames = "external_id")
        })
@AllArgsConstructor
@NoArgsConstructor
public class Loan extends BaseEntity {
    @SequenceGenerator(name = "loan_seq",
            sequenceName = "loan_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "loan_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "branch")
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

    @Column(name = "subject")
    @Enumerated(EnumType.STRING)
    private Subject subject;

    @Column(name = "resident")
    private Boolean resident;

    @Column(name = "is_auto_debit")
    private Boolean isAutoDebit = true;

    @ToString.Exclude
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<FeaturedLoans> featuredLoans = new HashSet<>();

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "is_insolvent")
    private Boolean isInsolvent;

    @ToString.Exclude
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<LoanGuarantor> guarantors;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "product_name")
    private String productName;

    public Loan(String extId, ClientInfoResponse nciLoanResponse, Language language, LoanInfoResponse loanInfoResponse) {
        this.branch = BranchUtils.getBranchByLoan(extId);
        this.pinfl = nciLoanResponse.getPinfl();
        this.tin = nciLoanResponse.getInn() != null ? nciLoanResponse.getInn() : "";
        this.birthday = nciLoanResponse.getBirthday();
        this.extId = extId;
        // this.payerName = nciLoanResponse.getFullName().toUpperCase();
        this.firstName = nciLoanResponse.getName().toUpperCase();
        this.patronymic = nciLoanResponse.getPatronymic().toUpperCase();
        this.surname = nciLoanResponse.getSurname().toUpperCase();
        this.payerName = this.surname + " " + this.firstName + " " + this.patronymic;
        this.isDeleted = false;
        this.passportSerial = nciLoanResponse.getPassportSerial();
        this.passportNumber = nciLoanResponse.getPassportNumber();
        this.payerPhone = nciLoanResponse.getPhoneMobile();
        this.idNibbd = nciLoanResponse.getIdNibbd() != null ? nciLoanResponse.getIdNibbd() : nciLoanResponse.getClientId();
        this.codeGender = nciLoanResponse.getCodeGender();
        this.birthPlace = nciLoanResponse.getBirthPlace();
        this.passportDateExpiration = nciLoanResponse.getPassportDateExpiration();
        this.passportDateRegistration = nciLoanResponse.getPassportDateRegistration();
        this.isConvicted = false;
        this.subject = nciLoanResponse.getSubject();
        this.resident = nciLoanResponse.getResident().equals("1");
        this.isAutoDebit = true;
        this.language = language;
        this.isInsolvent = false;
        this.currency = Currency.getCurrencyByCode(Integer.valueOf(loanInfoResponse.getCurrency()));
    }

    public Loan(String loanExtId, NCILoanInfoRepayResponse loanResult) {
        this.branch = BranchUtils.getBranchByLoan(loanExtId);
        this.pinfl = loanResult.getPersonCode();
        this.tin = loanResult.getInn() != null ? loanResult.getInn() : "";
        this.birthday = loanResult.getBirthday();
        this.extId = loanExtId;
        this.firstName = loanResult.getFirstName().toUpperCase();
        this.patronymic = loanResult.getPatronymic().toUpperCase();
        this.surname = loanResult.getSurname().toUpperCase();
        this.payerName = this.surname + " " + this.firstName + " " + this.patronymic;
        this.isDeleted = false;
        this.passportSerial = loanResult.getPassportSerial();
        this.passportNumber = loanResult.getPassportNumber();
        this.payerPhone = loanResult.getPhoneMobile();
        this.idNibbd = loanResult.getIdNibbd() != null ? loanResult.getIdNibbd() : loanResult.getClientId();
        this.codeGender = loanResult.getCodeGender();
        this.birthPlace = loanResult.getBirthPlace();
        this.passportDateExpiration = loanResult.getPassportDateExpiration();
        this.passportDateRegistration = loanResult.getPassportDateRegistration();
        this.isConvicted = false;
        this.subject = loanResult.getCodeSubject();
        this.resident = loanResult.getCodeResident().equals("1");
        this.isAutoDebit = true;
        this.language = Language.UZ;
        this.isInsolvent = false;
        this.currency = Currency.getCurrencyByCode(Integer.valueOf(loanResult.getCurrency()));
        this.productName = loanResult.getProductName() != null ? loanResult.getProductName().length() > 255 ? loanResult.getProductName().substring(0, 255) : loanResult.getProductName() : null;
    }
}
