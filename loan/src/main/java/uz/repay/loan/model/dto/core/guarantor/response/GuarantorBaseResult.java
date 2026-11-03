package uz.repay.loan.model.dto.core.guarantor.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Language;
import uz.repay.common.model.enums.Sex;
import uz.repay.loan.model.dto.core.guarantor.PassportData;
import uz.repay.loan.model.entity.Guarantor;
import uz.repay.loan.model.entity.LoanGuarantor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuarantorBaseResult {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("patronymic")
    private String patronymic;

    @JsonProperty("birthday")
    private LocalDate birthday;

    @JsonProperty("passport")
    private PassportData passport;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("language")
    private Language language;

    @JsonProperty("gender")
    private Sex gender;

    @JsonProperty("createdDate")
    private LocalDateTime createdDate;

    @JsonProperty("createdBy")
    private String createdBy;


    public GuarantorBaseResult(final Guarantor guarantor) {
        this.id = guarantor.getId();
        this.firstName = guarantor.getFirstName();
        this.surname = guarantor.getSurname();
        this.patronymic = guarantor.getPatronymic();
        this.birthday = guarantor.getBirthday();
        this.passport = guarantor.getPassport();
        this.phone = guarantor.getPhone();
        this.language = guarantor.getLanguage();
        this.gender = guarantor.getGender();
        this.createdDate = guarantor.getInitTime();
        this.createdBy = guarantor.getCreatedBy();
    }

    public GuarantorBaseResult(LoanGuarantor loanGuarantor) {
        var guarantor = loanGuarantor.getGuarantor();
        this.id = guarantor.getId();
        this.firstName = guarantor.getFirstName();
        this.surname = guarantor.getSurname();
        this.patronymic = guarantor.getPatronymic();
        this.birthday = guarantor.getBirthday();
        this.passport = guarantor.getPassport();
        this.phone = guarantor.getPhone();
        this.language = guarantor.getLanguage();
        this.gender = guarantor.getGender();
        this.createdDate = guarantor.getInitTime();
        this.createdBy = guarantor.getCreatedBy();
    }
}
