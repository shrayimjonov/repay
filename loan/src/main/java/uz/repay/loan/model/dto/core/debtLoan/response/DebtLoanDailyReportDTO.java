package uz.repay.loan.model.dto.core.debtLoan.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.loan.model.entity.domain.DebtLoanReport;
import uz.repay.loan.mongo.entity.DebtLoanDailyStats;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebtLoanDailyReportDTO {
    @JsonProperty("branch")
    @Schema(name = "branch", description = "branch of debt loans", example = "00981")
    private String branch;

    @JsonProperty("count")
    @Schema(name = "count", description = "count of debt loans", example = "25719")
    private Integer count;

    @JsonProperty("amount")
    @Schema(name = "amount", description = "amount in tiyin. Show to user divided by 100. (amount/100)", example = "-82429005790")
    private Long amount;

    @JsonProperty("created_date")
    @Schema(name = "createdDate", description = "Date of debt loans", example = "2024-10-05")
    private LocalDate createdDate;


    public DebtLoanDailyReportDTO(DebtLoanReport report) {
        this.branch = report.getBranch();
        this.count = report.getCount();
        this.amount = report.getDebt_amount();
        this.createdDate = report.getCreated_date();
    }

    public DebtLoanDailyReportDTO(DebtLoanDailyStats debtLoanDailyStats) {
        this.branch = debtLoanDailyStats.getBranch();
        this.count = debtLoanDailyStats.getCount();
        this.amount = debtLoanDailyStats.getAmount();
        this.createdDate = debtLoanDailyStats.getCreatedDate();
    }
}
