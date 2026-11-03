package uz.repay.report.model.dto.debtLoan.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebtLoanResponse {
    private Long id;
    private String payerName;
    private String payerPhone;
    private String loanExtId;
    private String branch;
    private Long recommended;
    private Long total;
    private String passport;
    private String pinfl;
    private LocalDateTime initTime;
    private LocalDate createdDate;
    private String createdBy;
}
