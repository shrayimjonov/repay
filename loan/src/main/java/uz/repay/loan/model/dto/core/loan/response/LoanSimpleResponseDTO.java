package uz.repay.loan.model.dto.core.loan.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Language;
import uz.repay.common.model.enums.Subject;
import uz.repay.loan.model.dto.core.guarantor.response.GuarantorShortResult;
import uz.repay.loan.model.entity.Loan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanSimpleResponseDTO {
    private Long id;
    private Branch branch;
    private String pinfl;
    private String tin;
    private String birthday;
    private String extId;
    private String payerName;
    private Boolean isDeleted;
    private String passport;
    private String payerPhone;
    private String idNibbd;
    private String codeGender;
    private String birthPlace;
    private String passportDateExpiration;
    private String passportDateRegistration;
    private Boolean isConvicted;
    private LocalDateTime initTime;
    private String createdBy;
    private Subject subject;
    private Boolean resident;
    private Boolean isAutoDebit;
    private Boolean isFeatured = false;
    private Language language;
    private Boolean isInsolvent;
    private List<GuarantorShortResult> guarantors = new ArrayList<>();

    public LoanSimpleResponseDTO(Loan loan) {
        this.id = loan.getId();
        this.branch = loan.getBranch();
        this.pinfl = loan.getPinfl();
        this.tin = loan.getTin();
        this.birthday = loan.getBirthday();
        this.extId = loan.getExtId();
        this.payerName = loan.getPayerName();
        this.isDeleted = loan.getIsDeleted();
        this.passport = loan.getPassportSerial() + loan.getPassportNumber();
        this.payerPhone = loan.getPayerPhone();
        this.idNibbd = loan.getIdNibbd();
        this.codeGender = loan.getCodeGender();
        this.birthPlace = loan.getBirthPlace();
        this.passportDateExpiration = loan.getPassportDateExpiration();
        this.passportDateRegistration = loan.getPassportDateRegistration();
        this.isConvicted = loan.getIsConvicted();
        this.initTime = loan.getInitTime();
        this.createdBy = loan.getCreatedBy();
        this.subject = loan.getSubject();
        this.resident = loan.getResident();
        this.isAutoDebit = loan.getIsAutoDebit();
        String username = SecurityUtils.getUsername();
        if (!username.equals("system") && !username.equals("external"))
            this.isFeatured = loan.getFeaturedLoans().stream().anyMatch(z -> z.getCreatedBy().equals(username));
        this.language = loan.getLanguage();
        this.isInsolvent = loan.getIsInsolvent();
        this.guarantors = new ArrayList<>(
                loan.getGuarantors()
                        .stream()
                        .map(GuarantorShortResult::new)
                        .toList()
        );
    }

    public LoanSimpleResponseDTO(Loan loan, Boolean isFeatured) {
        this.id = loan.getId();
        this.branch = loan.getBranch();
        this.pinfl = loan.getPinfl();
        this.tin = loan.getTin();
        this.birthday = loan.getBirthday();
        this.extId = loan.getExtId();
        this.payerName = loan.getPayerName();
        this.isDeleted = loan.getIsDeleted();
        this.passport = loan.getPassportSerial() + loan.getPassportNumber();
        this.payerPhone = loan.getPayerPhone();
        this.idNibbd = loan.getIdNibbd();
        this.codeGender = loan.getCodeGender();
        this.birthPlace = loan.getBirthPlace();
        this.passportDateExpiration = loan.getPassportDateExpiration();
        this.passportDateRegistration = loan.getPassportDateRegistration();
        this.isConvicted = loan.getIsConvicted();
        this.initTime = loan.getInitTime();
        this.createdBy = loan.getCreatedBy();
        this.subject = loan.getSubject();
        this.resident = loan.getResident();
        this.isAutoDebit = loan.getIsAutoDebit();
        this.isFeatured = isFeatured;
        this.language = loan.getLanguage();
        this.isInsolvent = loan.getIsInsolvent();
    }
}
