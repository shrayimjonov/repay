package uz.repay.autodebit.model.loan.response;

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
public class DebtLoansResult {
    private Long id;
    private String payerName;
    private String payerPhone;
    private String loanExtId;
    private String branch;
    private Long recommended;
    private Long total;
    private String passportSerial;
    private String passportNumber;
    private String pinfl;
    private LocalDateTime initTime;
    private LocalDate createdDate;
    private String createdBy;
    private Boolean isInsolvent;
    private Long loanId;

}
