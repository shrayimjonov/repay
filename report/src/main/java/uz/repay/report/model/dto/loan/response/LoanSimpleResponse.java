package uz.repay.report.model.dto.loan.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Language;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanSimpleResponse {
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
    private Language language;
    private Boolean isInsolvent;
}
