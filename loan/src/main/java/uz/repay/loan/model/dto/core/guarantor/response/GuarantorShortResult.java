package uz.repay.loan.model.dto.core.guarantor.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Language;
import uz.repay.loan.model.entity.LoanGuarantor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuarantorShortResult {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("language")
    private Language language;

    @JsonProperty("createdDate")
    private LocalDateTime createdDate;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;


    public GuarantorShortResult(final LoanGuarantor loanGuarantor) {
        var guarantor = loanGuarantor.getGuarantor();

        this.id = guarantor.getId();
        this.fullName = guarantor.getSurname() + " " + guarantor.getFirstName() + " " + guarantor.getPatronymic();
        this.phone = guarantor.getPhone();
        this.language = guarantor.getLanguage();
        this.createdDate = loanGuarantor.getInitTime();
        this.createdBy = loanGuarantor.getCreatedBy();
        this.isDeleted = loanGuarantor.getIsDeleted();
    }
}
