package uz.repay.loan.mongo.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import uz.repay.common.utils.DateUtils;
import uz.repay.loan.model.entity.domain.DebtLoanReport;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@ToString
@Document(collection = "debt_loan_daily_stats")
@AllArgsConstructor
@NoArgsConstructor
public class DebtLoanDailyStats {

    @Id
    private UUID id;

    @Indexed //(expireAfter = "P10D") //expire after 10 days, Example: "P2D" = 2 days or "PT30M" = 30 minutes or "PT10S" = 10 seconds
    private LocalDate createdDate = DateUtils.getDateNow();

    private Integer count;

    private Long amount;

    @Indexed
    private String branch;


    public DebtLoanDailyStats(DebtLoanReport report) {
        this.id = UUID.randomUUID();
        this.createdDate = report.getCreated_date();
        this.count = report.getCount();
        this.amount = report.getDebt_amount();
        this.branch = report.getBranch();
    }
}
