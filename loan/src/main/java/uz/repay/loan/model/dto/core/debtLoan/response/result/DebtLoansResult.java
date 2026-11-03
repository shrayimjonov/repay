package uz.repay.loan.model.dto.core.debtLoan.response.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.loan.model.entity.DebtLoan;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebtLoansResult {
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
    private Boolean isInsolvent;
    private Long loanId;

    public DebtLoansResult(DebtLoan debtLoan) {
        this.id = debtLoan.getId();
        this.payerName = debtLoan.getPayerName();
        this.payerPhone = debtLoan.getPayerPhone();
        this.loanExtId = debtLoan.getLoanExtId();
        this.branch = debtLoan.getBranch();
        this.recommended = debtLoan.getRecommended();
        this.total = debtLoan.getTotal();
        this.passport = debtLoan.getPassportSerial() + debtLoan.getPassportNumber();
        this.pinfl = debtLoan.getPinfl();
        this.initTime = debtLoan.getInitTime();
        this.createdDate = debtLoan.getCreatedDate();
        this.createdBy = debtLoan.getCreatedBy();
        this.isInsolvent = debtLoan.getIsInsolvent();
        this.loanId = debtLoan.getLoanId();
    }
}
