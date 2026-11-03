package uz.repay.loan.model.dto.core.loan.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.loan.model.entity.Loan;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanShortResponseDTO {
    private Long id;
    private String extId;
    private String payerName;
    private String payerPhone;
    private LocalDateTime initTime;
    private Boolean isFeatured = false;

    public LoanShortResponseDTO(Loan loan) {
        this.id = loan.getId();
        this.extId = loan.getExtId();
        this.payerName = loan.getPayerName();
        this.payerPhone = loan.getPayerPhone();
        this.initTime = loan.getInitTime();
        String username = SecurityUtils.getUsername();
        if (!username.equals("system") && !username.equals("external"))
            this.isFeatured = loan.getFeaturedLoans().stream().anyMatch(z -> z.getCreatedBy().equals(username));
    }
}
