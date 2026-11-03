package uz.repay.payment.model.dto.core.transaction.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.payment.model.dto.core.transaction.response.result.TransactionReportResult;
import uz.repay.payment.model.entity.domain.MonthlyReportDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReportDTO {

    private LocalDate date;
    private List<TransactionReportResult> data = new ArrayList<>();

    public TransactionReportDTO(MonthlyReportDTO t) {
        this.date = t.getDate().toLocalDate();
        this.data.add(new TransactionReportResult(t));
    }
}
